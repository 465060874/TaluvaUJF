
package ui;

import com.google.common.io.Files;
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

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IslandVisu extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        File file = new File("9947498794069.island");
        Island island = IslandIO.read(Files.asCharSource(file, StandardCharsets.UTF_8));

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
