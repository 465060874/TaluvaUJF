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

        private final EventHandler<MouseEvent> mouseClickedTile;
        private final EventHandler<MouseEvent> mouseClickedBuild;

        public FXUIPlayerHandler() {
            this.mouseClickedTile = this::mouseClickedTile;
            this.mouseClickedBuild = this::mouseClickedBuild;
        }

        @Override
        public void startTileStep() {
            if (engine.getIsland().isEmpty()) {
                SeaTileAction firstAction = engine.getSeaTileActions().get(0);
                engine.placeOnSea(firstAction);
                return;
            }

            gameView.getPlacement().placeTile(engine.getVolcanoTileStack().current());
            gameView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedTile);
        }

        @Override
        public void startBuildStep() {
            gameView.getPlacement().build(engine.getCurrentPlayer().getColor());
            gameView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedBuild);
        }

        @Override
        public void cancel() {
            gameView.getPlacement().cancel();
        }

        private void mouseClickedTile(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && gameView.getPlacement().isValid()) {
                gameView.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedTile);
                Action action = gameView.getPlacement().getAction();
                gameView.getPlacement().cancel();
                engine.action(action);
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                gameView.getPlacement().cycleTileOrientationOrBuildingType();
            }
        }

        private void mouseClickedBuild(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (gameView.getPlacement().isValid()) {
                    gameView.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedBuild);
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
