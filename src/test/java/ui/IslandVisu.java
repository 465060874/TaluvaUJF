
package ui;

import com.google.common.io.Resources;
import engine.Engine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import map.Island;
import map.IslandIO;
import ui.island.IslandView;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IslandVisu extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL rsc = Engine.class.getResource("EngineTest4.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        IslandView islandView = new IslandView(island, true);
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(islandView);

        Scene scene = new Scene(mainPane, 1000, 800, true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
