package ui;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import engine.rules.SeaPlacementRules;
import engine.rules.SeaPlacementRulesTest;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import map.Island;
import map.IslandIO;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class IslandVisu extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        File rsc = new File("16410698810380.island");
        Island island = IslandIO.read(Files.asCharSource(rsc, StandardCharsets.UTF_8));
        IslandCanvas canvas = new IslandCanvas(island, false);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(new IslandCanvasPane(canvas));

        Scene scene = new Scene(mainPane, 1000, 800, true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
