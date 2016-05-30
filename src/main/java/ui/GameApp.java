package ui;

import IA.IA;
import data.BuildingType;
import data.PlayerColor;
import engine.*;
import engine.action.Action;
import engine.action.SeaTileAction;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import map.Building;
import menu.Home4;
import menu.data.MenuData;
import ui.island.IslandSnapshot;

import java.io.File;
import java.io.IOException;

public class GameApp extends Application {

    private Engine engine;
    private GameView gameView;
    private Scene scene;
    private Stage stage;

    public GameApp() {
        this.engine = EngineBuilder.allVsAll()
                .player(PlayerColor.BROWN, IA.DIFFICILE)
                .player(PlayerColor.WHITE, IA.DIFFICILE)
                .build();
    }

    public GameApp(MenuData menuData) {
        this.engine = menuData.engineBuilder(new FXPlayerHandler())
                .build();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.gameView = new GameView(engine);
        gameView.getHomeButton().setOnAction(this::goHome);
        gameView.getSaveButton().setOnAction(this::save);

        this.scene = new Scene(gameView, 1000, 800);
        stage.setResizable(true);
        stage.setX(stage.getX() - (scene.getWidth() - stage.getWidth()) / 2);
        stage.setY(stage.getY() - (scene.getHeight() - stage.getHeight()) / 2);
        stage.setWidth(scene.getWidth());
        stage.setHeight(scene.getHeight());
        stage.setScene(scene);
        engine.start();
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    private void goHome(ActionEvent actionEvent) {
        Home4 home = new Home4();
        home.start(stage);
    }

    private void save(ActionEvent event) {
        File outputDir = new File("Saves");
        String basename = Long.toString(System.currentTimeMillis());
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
            return new FXUIPlayerTurn(step);
        }
    }

    public class FXUIPlayerTurn implements PlayerTurn {

        private final EventHandler<MouseEvent> mousePressed;
        private final EventHandler<MouseEvent> mouseDragged;
        private final EventHandler<MouseEvent> mouseReleasedTile;
        private final EventHandler<MouseEvent> mouseReleasedBuild;

        private boolean dragged;

        public FXUIPlayerTurn(EngineStatus.TurnStep step) {
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
