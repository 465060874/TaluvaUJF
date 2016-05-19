package ui;

import com.google.common.io.Resources;
import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import map.*;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Editor extends Application {


    private FieldType[] fields;
    private int fieldToPaint = -1;
    private BuildingType[] buildings;
    private int buildingToPaint;
    Point2D click;
    Hex clickedHex;


    @Override
    public void start(Stage stage) throws Exception {
        URL rsc = FXUI.class.getResource("test.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        IslandCanvasEditor canvas = new IslandCanvasEditor(island, true);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(new IslandCanvasPane(canvas));

        Scene scene = new Scene(mainPane, 1000, 800, true, SceneAntialiasing.BALANCED);

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            click = new Point2D(e.getSceneX(), e.getSceneY());
            System.out.println(click.getX() + " " + click.getY());

            final double l  = canvas.getTranslateX() + e.getSceneX();
            final double d = canvas.getTranslateX() + e.getSceneY();
            //final Hex hex = canvas.pointToHex(l, d);
            //island.putTile(new VolcanoTile(FieldType.JUNGLE, FieldType.JUNGLE), hex, Orientation.NORTH);
            canvas.redraw();
        });


        //hut = new house{};

        // Top Menu
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        mainPane.setTop(menuBar);

        Menu fileMenu = new Menu("Fichier");
        MenuItem newMenuItem = new MenuItem("Nouveau");
        MenuItem openMenuItem = new MenuItem("Ouvrir");
        MenuItem saveMenuItem = new MenuItem("Sauvegarder");
        MenuItem exitMenuItem = new MenuItem("Quitter");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(newMenuItem, saveMenuItem, openMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        Menu editMenu = new Menu("Edition");
        MenuItem undoItem = new MenuItem("Annuler");
        MenuItem redoItem = new MenuItem("Répéter");

        editMenu.getItems().addAll(undoItem, redoItem);

        // Pour dessiner les différentes Tiles
        fields = FieldType.values();
        buildings = BuildingType.values();

        Menu tileMenu = new Menu("Draw");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem[] fieldTypeToggles = new RadioMenuItem[fields.length + buildings.length];
        for (int i = 0; i < fields.length; i++) {
            fieldTypeToggles[i] = new RadioMenuItem(fields[i].toString());
            fieldTypeToggles[i].setToggleGroup(toggleGroup);
            final int ifinal = i;
            fieldTypeToggles[i].setOnAction(e -> changeFieldToPaint(ifinal));
        }

        for (int j = 0; j < buildings.length; j++) {
            fieldTypeToggles[j + fields.length] = new RadioMenuItem(buildings[j].toString());
            fieldTypeToggles[j + fields.length].setToggleGroup(toggleGroup);
            final int jFinal = j;
            fieldTypeToggles[j + fields.length].setOnAction(e -> changeBuildToPaint(jFinal));
        }

        tileMenu.getItems().addAll(fieldTypeToggles);
        menuBar.getMenus().addAll(fileMenu, editMenu, tileMenu);

        stage.setScene(scene);
        stage.show();
    }

    private void changeFieldToPaint(final int fieldTypeOrder) {
        fieldToPaint = fieldTypeOrder;
    }

    private void changeBuildToPaint(final int buildingTypeOrder) {
        buildingToPaint = buildingTypeOrder;
    }

    public static void main(String[] args) {
        launch(args);
    }




    private class IslandCanvasEditor extends IslandCanvas {


        IslandCanvasEditor(Island island, boolean debug) {
            super(island, debug);
        }

        private void mouseMoved(MouseEvent event) {
            click = new Point2D(event.getSceneX(), event.getSceneY());
            System.out.println("Dragged " + click.getX() + " " + click.getY());
            redraw();

        }

    }

    //TODO En cours



}
