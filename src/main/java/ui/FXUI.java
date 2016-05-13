package ui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FXUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MapCanvas canvas = new MapCanvas();

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(new ResizeCanvasPane(canvas));

        Scene scene = new Scene(mainPane, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
