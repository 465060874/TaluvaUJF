package ui;

import IA.BotPlayerHandler;
import com.google.common.io.Files;
import data.PlayerColor;
import engine.*;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import engine.tilestack.VolcanoTileStack;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.island.IslandView;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class EngineVisu extends Application implements EngineObserver {

    private Engine engine;
    private IslandView islandView;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        VolcanoTileStack.Factory stackFactory = VolcanoTileStack.read(
                Files.asCharSource(new File("stack"), StandardCharsets.UTF_8));
        this.engine = EngineBuilder.allVsAll()
                .logLevel(Level.INFO)
                .tileStack(stackFactory)
                .seed(8006646545592930086L)
                .player(PlayerColor.RED, uiWrap(BotPlayerHandler.factory(16, 1)))
                .player(PlayerColor.WHITE, uiWrap(BotPlayerHandler.factory(16, 2)))
                .build();
        engine.registerObserver(new EngineLoggerObserver(engine));
        engine.registerObserver(this);

        this.islandView = new IslandView(engine.getIsland(), true);
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
        islandView.redrawIsland();
    }

    @Override
    public void onTileStackChange(boolean cancelled) {
    }

    @Override
    public void onTileStepStart(boolean cancelled) {
        islandView.redrawIsland();
    }

    @Override
    public void onBuildStepStart(boolean cancelled) {
        islandView.redrawIsland();
    }

    @Override
    public void onTilePlacementOnSea(SeaTileAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onBuild(PlaceBuildingAction action) {
        islandView.redrawIsland();
    }

    @Override
    public void onExpand(ExpandVillageAction action) {
        islandView.redrawIsland();
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

        private final PlayerHandler wrapped;
        private final EventHandler<MouseEvent> startWrappedTileStep;
        private final EventHandler<MouseEvent> startWrappedBuildStep;

        private UIPlayerHandlerWrapper(PlayerHandler wrapped) {
            this.startWrappedTileStep = this::startWrappedTileStep;
            this.startWrappedBuildStep = this::startWrappedBuildStep;
            this.wrapped = wrapped;
        }

        @Override
        public void startTileStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
        }

        private void startWrappedTileStep(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
                wrapped.startTileStep();
            }
        }

        @Override
        public void startBuildStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }

        private void startWrappedBuildStep(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
                wrapped.startBuildStep();
            }
        }

        @Override
        public void cancel() {
            wrapped.cancel();
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }
    }
}
