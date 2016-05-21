package ui;

import IA.BotPlayerHandler;
import data.PlayerColor;
import data.VolcanoTile;
import engine.*;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
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
                .logLevel(Level.FINE)
                .player(PlayerColor.RED, uiWrap(PlayerHandler.dumbFactory()))
                .player(PlayerColor.WHITE, uiWrap(BotPlayerHandler::new))
                .build();
        /*CharSource gameSource = Files.asCharSource(new File("9233261640382.taluva"), StandardCharsets.UTF_8);
        this.engine = EngineRecord.load(gameSource).replay();*/
        engine.registerObserver(new EngineLoggerObserver(engine));
        engine.registerObserver(this);

        this.islandView = new IslandView(engine.getIsland(), false);
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(islandView);

        this.scene = new Scene(mainPane, 1000, 800, true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);
        stage.setOnShown(event -> engine.start());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onStart() {
        islandView.islandCanvas.redraw();
    }

    @Override
    public void onTileStackChange() {
        VolcanoTile tile = engine.getVolcanoTileStack().current();
        engine.logger().info("Drawn : {0} {1}", tile.getLeft(), tile.getRight());
    }

    @Override
    public void onTileStepStart() {
    }

    @Override
    public void onBuildStepStart() {
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
    public void onWin(WinReason reason, List<Player> winners) {
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
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
            playerHandler.startTileStep();
        }

        @Override
        public void startBuildStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }

        private void startWrappedBuildStep(MouseEvent event) {
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
            playerHandler.startBuildStep();
        }

        @Override
        public void cancel() {
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }
    }
}
