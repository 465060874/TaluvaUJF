package ui;

import IA.BotPlayerHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class EngineVisu extends Application implements EngineObserver {

    private Engine engine;
    private IslandView islandView;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        this.engine = new EngineBuilder()
                .gamemode(Gamemode.TwoPlayer)
                .player(PlayerColor.RED, (engine) -> new UIDumbPlayerHandler(PlayerHandler.dumbFactory().create(engine)))
                .player(PlayerColor.WHITE, (engine) -> new UIDumbPlayerHandler(new BotPlayerHandler(engine)))
                .build();
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
    }

    @Override
    public void onTileStackChange() {
        System.out.println("Drawn : " + engine.getVolcanoTileStack().current());
    }

    @Override
    public void onTileStepStart() {
    }

    @Override
    public void onBuildStepStart() {
    }

    @Override
    public void onTilePlacementOnSea(SeaTileAction action) {
        islandView.canvas.redraw();
    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction action) {
        islandView.canvas.redraw();
    }

    @Override
    public void onBuild(PlaceBuildingAction action) {
        islandView.canvas.redraw();
    }

    @Override
    public void onExpand(ExpandVillageAction action) {
        islandView.canvas.redraw();
    }

    @Override
    public void onEliminated(Player eliminated) {

    }

    @Override
    public void onWin(WinReason reason, List<Player> winners) {
        System.out.println("Winner : " + winners.stream()
                .map(Player::getColor)
                .collect(toList())
                .toString());
    }

    class UIDumbPlayerHandler implements PlayerHandler {

        private final PlayerHandler playerHandler;
        private final EventHandler<MouseEvent> startTileStepHandler;
        private final EventHandler<MouseEvent> startBuildStepHandler;

        public UIDumbPlayerHandler(PlayerHandler subHandler) {
            this.startTileStepHandler = this::doStartTileStep;
            this.startBuildStepHandler = this::doStartBuildStep;
            this.playerHandler = subHandler;
        }

        @Override
        public void startTileStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startTileStepHandler);
        }

        private void doStartTileStep(MouseEvent event) {
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startTileStepHandler);
            playerHandler.startTileStep();
        }

        @Override
        public void startBuildStep() {
            scene.addEventHandler(MouseEvent.MOUSE_CLICKED, startBuildStepHandler);
        }

        private void doStartBuildStep(MouseEvent event) {
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startBuildStepHandler);
            playerHandler.startBuildStep();
        }

        @Override
        public void cancel() {
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startTileStepHandler);
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, startBuildStepHandler);
        }
    }
}
