package ui;

import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import map.Island;
import map.IslandIO;
import map.IslandTest;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IslandVisu extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL rsc = IslandTest.class.getResource("IslandTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        IslandCanvas canvas = new IslandCanvas(island, true);
        FreeTileCanvas freeTileCanvas = new FreeTileCanvas(island, true);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(new IslandCanvasPane(canvas, freeTileCanvas));

        Scene scene = new Scene(mainPane, 1000, 800, true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
