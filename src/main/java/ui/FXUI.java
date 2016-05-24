package ui;

import com.google.common.io.Resources;
import data.PlayerColor;
import engine.Engine;
import engine.EngineBuilder;
import engine.PlayerHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import map.Island;
import map.IslandIO;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FXUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL rsc = FXUI.class.getResource("test.island");
        Island island1 = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .island(island1)
                .player(PlayerColor.BROWN, e -> PlayerHandler.dummy())
                .player(PlayerColor.WHITE, e -> PlayerHandler.dummy())
                .player(PlayerColor.YELLOW, e -> PlayerHandler.dummy())
                .player(PlayerColor.RED, e -> PlayerHandler.dummy())
                .build();
        engine.start();

        GameView gameView = new GameView(engine);

        Scene scene = new Scene(gameView, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
