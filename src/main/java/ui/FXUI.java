package ui;

import com.google.common.io.Resources;
import data.PlayerColor;
import engine.Engine;
import engine.EngineBuilder;
import engine.PlayerHandler;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import map.Island;
import map.IslandIO;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FXUI extends Application {

    private GameView gameView;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        URL rsc = FXUI.class.getResource("test.island");
        Island island1 = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .island(island1)
                .player(PlayerColor.BROWN, uiWrap(PlayerHandler.dumbFactory()))
                .player(PlayerColor.WHITE, uiWrap(PlayerHandler.dumbFactory()))
                .player(PlayerColor.YELLOW, uiWrap(PlayerHandler.dumbFactory()))
                .player(PlayerColor.RED, uiWrap(PlayerHandler.dumbFactory()))
                .build();

        this.gameView = new GameView(engine);

        this.scene = new Scene(gameView, 1000, 800);
        stage.setScene(scene);
        stage.setOnShowing(e -> engine.start());
        stage.show();
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
            gameView.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
        }

        private void startWrappedTileStep(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                gameView.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
                wrapped.startTileStep();
            }
        }

        @Override
        public void startBuildStep() {
            gameView.addEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }

        private void startWrappedBuildStep(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && !event.isControlDown()) {
                gameView.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
                wrapped.startBuildStep();
            }
        }

        @Override
        public void cancel() {
            wrapped.cancel();
            gameView.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedTileStep);
            gameView.removeEventHandler(MouseEvent.MOUSE_CLICKED, startWrappedBuildStep);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
