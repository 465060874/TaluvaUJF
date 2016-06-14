package ui;

import com.google.common.io.Files;
import data.BuildingType;
import data.PlayerColor;
import engine.*;
import engine.action.Action;
import engine.action.SeaTileAction;
import engine.record.EngineRecorder;
import ia.IA;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import map.Building;
import menu.HomeVF;
import menu.data.MenuData;
import ui.island.IslandSnapshot;
import util.FxUncaughtExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GameApp extends Application {

    private final Engine engine;
    private final EngineRecorder recorder;

    private GameView gameView;
    private Scene scene;
    private Stage stage;

    public GameApp() {
        FXPlayerHandler handler = new FXPlayerHandler();
        this.engine = EngineBuilder.allVsAll()
                .player(PlayerColor.WHITE, IA.DIFFICILE)
                .player(PlayerColor.YELLOW, IA.DIFFICILE)
                .build();
        this.recorder = EngineRecorder.install(engine);
    }

    public GameApp(MenuData menuData) {
        this.engine = menuData.engineBuilder(new FXPlayerHandler())
                .build();
        this.recorder = EngineRecorder.install(engine);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxUncaughtExceptionHandler.install(engine);

        this.stage = stage;
        this.gameView = new GameView(engine);
        gameView.getHomeButton().setOnAction(this::goHome);
        gameView.getSaveButton().setOnAction(this::save);

        this.scene = new Scene(gameView, 800, 600);
        scene.setOnKeyReleased(this::fullscreen);
        stage.setResizable(true);
        stage.setX(Math.max(0, stage.getX() - (scene.getWidth() - stage.getWidth()) / 2));
        stage.setY(Math.max(0, stage.getY() - (scene.getHeight() - stage.getHeight()) / 2));
        stage.setWidth(scene.getWidth());
        stage.setHeight(scene.getHeight());
        stage.setScene(scene);
        engine.start();
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    private void fullscreen(KeyEvent event) {
        if (event.getCode() == KeyCode.F)
            stage.setFullScreen(true);
    }

    private void goHome(ActionEvent actionEvent) {
        HomeVF home = new HomeVF();
        home.start(stage);
    }

    private void save(ActionEvent event) {
        File outputDir = new File("Saves");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Error while trying to save game");
        }

        String basename = Long.toString(System.currentTimeMillis());

        File recordFile = new File(outputDir, basename + ".taluva");
        recorder.getRecord().save(Files.asCharSink(recordFile, StandardCharsets.UTF_8));

        try {
            IslandSnapshot.take(engine.getIsland(), outputDir, basename);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class FXPlayerHandler implements PlayerHandler {

        @Override
        public boolean isHuman() {
            return true;
        }

        @Override
        public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
            return new FXPlayerTurn(step);
        }
    }

    public class FXPlayerTurn implements PlayerTurn {

        private final EventHandler<MouseEvent> mousePressed;
        private final EventHandler<MouseEvent> mouseDragged;
        private final EventHandler<MouseEvent> mouseReleasedTile;
        private final EventHandler<MouseEvent> mouseReleasedBuild;

        private boolean dragged;

        public FXPlayerTurn(EngineStatus.TurnStep step) {
            this.mousePressed = this::mousePressed;
            this.mouseDragged = this::mouseDragged;
            this.mouseReleasedTile = this::mouseReleasedTile;
            this.mouseReleasedBuild = this::mouseReleasedBuild;

            this.dragged = false;

            gameView.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            gameView.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);

            if (step == EngineStatus.TurnStep.TILE) {
                prepareTileStep();
            }
            else {
                prepareBuildStep();
            }
        }

        @Override
        public void cancel() {
            gameView.getPlacement().cancel();
            gameView.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            gameView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
            gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedTile);
            gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedBuild);
        }

        private void mousePressed(MouseEvent mouseEvent) {
            dragged = false;
        }

        private void mouseDragged(MouseEvent mouseEvent) {
            if (!dragged && gameView.isMouseDragged()) {
                dragged = true;
            }
        }

        private void prepareTileStep() {
            if (engine.getIsland().isEmpty()) {
                SeaTileAction firstAction = engine.getSeaTileActions().get(0);
                engine.placeOnSea(firstAction);
                prepareBuildStep();
            }
            else {
                gameView.getPlacement().placeTile(engine.getVolcanoTileStack().current());
                gameView.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedTile);
            }
        }

        private void prepareBuildStep() {
            gameView.getPlacement().build(engine.getCurrentPlayer().getColor());
            gameView.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedBuild);
        }

        private void mouseReleasedTile(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && gameView.getPlacement().isValid()) {
                if (dragged) {
                    dragged = false;
                    return;
                }

                gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedTile);
                Action action = gameView.getPlacement().getAction();
                gameView.getPlacement().cancel();
                engine.action(action);
                prepareBuildStep();
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                gameView.getPlacement().cycleTileOrientationOrBuildingType();
            }
        }

        private void mouseReleasedBuild(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (dragged) {
                    return;
                }

                if (gameView.getPlacement().isValid()) {
                    gameView.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
                    gameView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
                    gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedBuild);
                    Action action = gameView.getPlacement().getAction();
                    gameView.getPlacement().cancel();
                    engine.action(action);
                    return;
                }

                Building building = engine.getIsland().getField(gameView.getPlacement().getHex()).getBuilding();
                if (building.getType() != BuildingType.NONE
                        && building.getColor() == engine.getCurrentPlayer().getColor()) {
                    gameView.getPlacement().expand(engine.getIsland().getVillage(gameView.getPlacement().getHex()));
                }
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                gameView.getPlacement().cycleTileOrientationOrBuildingType();
            }
        }
    }
}
