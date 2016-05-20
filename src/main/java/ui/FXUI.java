package ui;

import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import map.Island;
import map.IslandIO;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FXUI extends Application {

    private static final boolean DEBUG = false;

    @Override
    public void start(Stage stage) throws Exception {
        URL rsc = FXUI.class.getResource("test.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        IslandView islandView = new IslandView(island, DEBUG);
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(islandView);

        Scene scene = new Scene(mainPane, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
