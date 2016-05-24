package ui;

import data.PlayerColor;
import engine.*;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;

public class EngineVisu extends Application implements EngineObserver {

    private Engine engine;
    private IslandView islandView;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        this.engine = EngineBuilder.allVsAll()
                .logLevel(Level.INFO)
                //.seed(8006726615907585890L)
                .player(PlayerColor.RED, uiWrap(PlayerHandler.dumbFactory()))
                .player(PlayerColor.WHITE, uiWrap(PlayerHandler.dumbFactory()))
                .build();
        engine.registerObserver(new EngineLoggerObserver(engine));
        engine.registerObserver(this);

        this.islandView = new IslandView(engine.getIsland(), false);
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(islandView);

        this.scene = new Scene(mainPane, 1000, 800, true, SceneAntialiasing.BALANCED);
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, this::cancelLastStep);
        stage.setScene(scene);
        stage.setOnShown(event -> engine.start());
        stage.show();
    }

    private void cancelLastStep(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.isControlDown()) {
            engine.cancelLastStep();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onStart() {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onTileStackChange(boolean cancelled) {
    }

    @Override
    public void onTileStepStart(boolean cancelled) {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onBuildStepStart(boolean cancelled) {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onTilePlacementOnSea(SeaTileAction action) {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction action) {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onBuild(PlaceBuildingAction action) {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onExpand(ExpandVillageAction action) {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onEliminated(Player eliminated) {
    }

    @Override
    public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
    }

    private PlayerHandler.Factory uiWrap(PlayerHandler.Factory factory) {
        return (engine) -> new UIPlayerHandlerWrapper(factory.create(engine));
    }

    private class UIPlayerHandlerWrapper implements PlayerHandler {

        private final PlayerHandler playerHandler;
        private final EventHandler<MouseEvent> startWrappedTileStep;
        private final EventHandler<MouseEvent> startWrappedBuildStep;

        private UIPlayerHandlerWrapper(PlayerHandler subHandler) {
            this.startWrappedTileStep = this::startWrappedTileStep;
            this.startWrappedBuildStep = this::startWrappedBuildStep;
            this.playerHandler = subHandler;
        }

        @Override
        public void startTileStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
        }

        private void startWrappedTileStep(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
                playerHandler.startTileStep();
            }
        }

        @Override
        public void startBuildStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }

        private void startWrappedBuildStep(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
                playerHandler.startBuildStep();
            }
        }

        @Override
        public void cancel() {
            playerHandler.cancel();
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }
    }
}
