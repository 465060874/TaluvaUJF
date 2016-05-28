package ui;

import IA.BotPlayerHandler;
import data.BuildingType;
import data.PlayerColor;
import engine.Engine;
import engine.EngineBuilder;
import engine.PlayerHandler;
import engine.action.Action;
import engine.action.SeaTileAction;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import map.Building;

public class FXUI extends Application {

    private Engine engine;
    private GameView gameView;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        this.engine = EngineBuilder.allVsAll()
                .player(PlayerColor.BROWN, e -> new FXUIPlayerHandler())
                .player(PlayerColor.WHITE, BotPlayerHandler.factory(16, 1))
                .build();

        this.gameView = new GameView(engine);

        this.scene = new Scene(gameView, 1000, 800);
        stage.setScene(scene);
        stage.setOnShowing(e -> engine.start());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class FXUIPlayerHandler implements PlayerHandler {

        private final EventHandler<MouseEvent> mousePressed;
        private final EventHandler<MouseEvent> mouseDragged;
        private final EventHandler<MouseEvent> mouseReleasedTile;
        private final EventHandler<MouseEvent> mouseReleasedBuild;

        private boolean dragged;

        public FXUIPlayerHandler() {
            this.mousePressed = this::mousePressed;
            this.mouseDragged = this::mouseDragged;
            this.mouseReleasedTile = this::mouseReleasedTile;
            this.mouseReleasedBuild = this::mouseReleasedBuild;
        }

        private void mousePressed(MouseEvent mouseEvent) {
            dragged = false;
        }

        private void mouseDragged(MouseEvent mouseEvent) {
            dragged = true;
        }

        @Override
        public void startTileStep() {
            if (engine.getIsland().isEmpty()) {
                SeaTileAction firstAction = engine.getSeaTileActions().get(0);
                engine.placeOnSea(firstAction);
                return;
            }

            gameView.getPlacement().placeTile(engine.getVolcanoTileStack().current());
            gameView.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            gameView.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
            gameView.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedTile);
        }

        @Override
        public void startBuildStep() {
            gameView.getPlacement().build(engine.getCurrentPlayer().getColor());
            gameView.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            gameView.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
            gameView.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedBuild);
        }

        @Override
        public void cancel() {
            gameView.getPlacement().cancel();
            gameView.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
            gameView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
            gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedTile);
            gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedBuild);
        }

        private void mouseReleasedTile(MouseEvent event) {

            if (event.getButton() == MouseButton.PRIMARY && gameView.getPlacement().isValid()) {
                if (dragged) {
                    return;
                }

                gameView.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
                gameView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
                gameView.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedTile);
                Action action = gameView.getPlacement().getAction();
                gameView.getPlacement().cancel();
                engine.action(action);
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
