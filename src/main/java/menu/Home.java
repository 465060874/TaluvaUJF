package menu;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
//import javafx.scene.layout.HBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Created by zhaon on 18/05/16.
 */
public class Home extends Application{

    int largeurs = 800;
    int hauteurs = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        stage.setTitle("TALUVA");
        Group root = new Group();
        Scene scene = new Scene(root, largeurs, hauteurs);
        //scene.setFill(Color.rgb(169, 234, 254));
        scene.getStylesheets().add("/menu/menus.css");


        int lv = largeurs/2;

        //Logo
        Image logo = new Image(getClass().getResourceAsStream("logo.jpg"));

        ImageView iv1 = new ImageView();
        iv1.setImage(logo);
        iv1.setFitWidth(lv);
        iv1.setFitHeight(120);
        VBox vlogo = new VBox();
        //vlogo.setAlignment(Pos.CENTER);
        vlogo.getChildren().add(iv1);
        //Button
        //Image imageOk = new Image(getClass().getResourceAsStream("/menu/fb.png"));
        //Button breprendre = new Button("REPRENDRE",new ImageView(imageOk));
        Button breprendre = new Button();
        //breprendre.setPrefWidth(lv);


        //breprendre.setLayoutX(5);
        //breprendre.setLayoutY(10);
        breprendre.setPrefWidth(100);
        breprendre.setPrefHeight(50);
        System.out.println(breprendre.getLayoutX());
        System.out.println(breprendre.getLayoutY());

        Arc arc = new Arc();
        arc.setCenterX(50.0f);
        arc.setCenterY(50.0f);
        arc.setRadiusX(25.0f);
        arc.setRadiusY(25.0f);
        arc.setStartAngle(45.0f);
        arc.setLength(270.0f);
        arc.setType(ArcType.ROUND);

        /*double[] path = new double[48];
        for (int q = 0; q < 24; q++) {
            double x = Math.cos(Math.PI / 3.0 * q + Math.PI / 2.0);
            double y = Math.sin(Math.PI / 3.0 * q + Math.PI / 2.0);
            path[q * 2] = x;
            path[q * 2 + 1] = y;
        }

        Polygon aPoly = new Polygon(path);*/
        //breprendre.setShape(aPoly);
        breprendre.setShape(arc);

        //breprendre.setRotate(5);
        //breprendre.setTranslateZ(30);
        //breprendre.setTranslateX(60);
        //breprendre.setStyle("-fx-background-image: url('/menu/fb.jpg')");
        //BackgroundImage backgroundImage = new BackgroundImage( new Image( getClass().getResource("/menu/fb.jpg").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        //Background background = new Background(backgroundImage);
        //breprendre.setBackground(background);
        breprendre.setScaleX(0.5);
        breprendre.setCacheShape(true);
        breprendre.getStyleClass().add("breprendre");
        VBox vreprendre = new VBox();
        vreprendre.setAlignment(Pos.CENTER);
        vreprendre.getChildren().add(breprendre);

        //Accordion

        //grid1
        GridPane grid1 = new GridPane();
        VBox vBoxg = new VBox(1);
        vBoxg.setPrefWidth(lv);


        HBox hBoxg = new HBox(2);
        hBoxg.setPrefWidth(lv);
        hBoxg.setAlignment(Pos.CENTER);

        HBox hBoxh = new HBox(2);
        hBoxh.setAlignment(Pos.CENTER);
        VBox vBoxhg = new VBox();
        vBoxhg.setPrefWidth(lv);
        vBoxhg.setAlignment(Pos.CENTER);
        vBoxhg.getChildren().addAll(new Label("ICONE"));
        VBox vBoxhd = new VBox();
        vBoxhd.setPrefWidth(lv);
        vBoxhd.setAlignment(Pos.CENTER);
        vBoxhd.getChildren().addAll(new Label("NIVEAUX"));
        hBoxh.getChildren().addAll(vBoxhg,vBoxhd);
        vBoxg.getChildren().add(hBoxh);

        VBox vBoxgg = new VBox(5);
       // vBoxgg.setStyle("-fx-border-color: pink;" +
         //       "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxgg.setPadding(new Insets(10, 10, 10, 10));
        vBoxgg.setPrefWidth(lv);
        vBoxgg.setAlignment(Pos.CENTER);


        Button bicone = new Button();
        bicone.setPrefSize(lv,100);
        Image imageDecline = new Image(getClass().getResourceAsStream("icone1.jpg"));
        bicone.setGraphic(new ImageView(imageDecline));
        vBoxgg.getChildren().addAll(bicone);

        VBox vBoxgd = new VBox(5);
       // vBoxgd.setStyle("-fx-border-color: pink;" +
         //       "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxgd.setPadding(new Insets(10, 10, 10, 10));
        vBoxgd.setPrefWidth(lv);
        vBoxgd.setAlignment(Pos.CENTER);
        ToggleButton simple = new ToggleButton("SIMPLE");
        simple.setPrefWidth(150);
        ToggleButton moyen = new ToggleButton("MOYEN");
        moyen.setPrefWidth(150);
        ToggleButton difficile = new ToggleButton("DIFFICILE");
        difficile.setPrefWidth(150);
        vBoxgd.getChildren().addAll(simple,moyen,difficile);
        simple.getStyleClass().add("buttonniveaux1");
        moyen.getStyleClass().add("buttonniveaux2");
        difficile.getStyleClass().add("buttonniveaux3");

        hBoxg.getChildren().addAll(vBoxgg,vBoxgd);
        vBoxg.getChildren().add(hBoxg);
        grid1.getChildren().add(vBoxg);

        //grid2
        GridPane grid2 = new GridPane();
        VBox vBoxg2 = new VBox(5);
        vBoxg2.setAlignment(Pos.CENTER);
        vBoxg2.setPrefWidth(largeurs);
        Button unjoueur = new Button("[JOUEUR1]");
        unjoueur.setPrefWidth(largeurs);
        Button deuxjoueurs = new Button("[JOUEUR1]  [JOUEUR2]");
        deuxjoueurs.setPrefWidth(largeurs);
        Button troisjoueurs = new Button("[JOUEUR1]  [JOUEUR2]  [JOUEUR3]");
        troisjoueurs.setPrefWidth(largeurs);
        Button quatrejoueurs1 = new Button("[JOUEUR1]  [JOUEUR2]  [JOUEUR3]  [JOUEUR4]");
        quatrejoueurs1.setPrefWidth(largeurs);
        Button quatrejoueurs2 = new Button("[JOUEUR1 / JOUEUR2]     [JOUEUR3 / JOUEUR4]");
        quatrejoueurs2.setPrefWidth(largeurs);

        unjoueur.getStyleClass().add("buttonjoueur");
        deuxjoueurs.getStyleClass().add("buttonjoueur");
        troisjoueurs.getStyleClass().add("buttonjoueur");
        quatrejoueurs1.getStyleClass().add("buttonjoueur");
        quatrejoueurs2.getStyleClass().add("buttonjoueur");

        vBoxg2.getChildren().addAll(unjoueur,deuxjoueurs,troisjoueurs,quatrejoueurs1,quatrejoueurs2);
        grid2.getChildren().add(vBoxg2);


        //grid3
        ScrollBar sc = new ScrollBar();
        sc.orientationProperty().set(Orientation.VERTICAL);
        GridPane grid3 = new GridPane();
        grid3.getChildren().add(sc);
        VBox vBoxg3 = new VBox(5);
        vBoxg3.setAlignment(Pos.CENTER);
        vBoxg3.setPrefWidth(lv);
        vBoxg3.setStyle("-fx-border-color: pink;" +
                "-fx-border-style: solid;-fx-border-width: 5;");
        Rectangle rec1 =  new Rectangle(lv/2, 50);
        Rectangle rec2 =  new Rectangle(lv/2, 50);
        Rectangle rec3 =  new Rectangle(lv/2, 50);
        vBoxg3.getChildren().addAll(rec1,rec2,rec3);
        grid3.getChildren().add(vBoxg3);


        TitledPane t1 = new TitledPane("SOLO",grid1);
        t1.setAlignment(Pos.CENTER);
        //t1.setShape(aPoly);
        TitledPane t2 = new TitledPane("MULTIJOUEUR", grid2);
        t2.setAlignment(Pos.CENTER);
        TitledPane t3 = new TitledPane("CHANGER", grid3);
        t3.setAlignment(Pos.CENTER);
        t1.getStyleClass().add("tp");
        t2.getStyleClass().add("tp");
        t3.getStyleClass().add("tp");
        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(t1, t2, t3);

        for (TitledPane titledPane : accordion.getPanes()) {
            titledPane.setOnMouseEntered(e -> titledPane.setExpanded(true));
            titledPane.setOnMouseExited(e -> titledPane.setExpanded(false));
        }

        VBox vBox = new VBox(10);
        //vBox.setStyle("-fx-border-color: pink;" +
        //        "-fx-border-style: solid;-fx-border-width: 5;");

        VBox vvide = new VBox();
        vvide.setPrefSize(largeurs/2,100);

        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setMaxWidth(largeurs/2);
        //vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(vvide,vlogo,vreprendre,accordion);


        VBox vBoxd = new VBox(50);
        vBoxd.setStyle("-fx-border-color: pink;" +
                "-fx-border-style: solid;-fx-border-width: 5;");
        vBoxd.setPadding(new Insets(10, 10, 10, 10));
        vBoxd.setPrefWidth(400);



        HBox hBox = new HBox(30);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setPrefSize(largeurs,hauteurs);
        hBox.setStyle("-fx-background-image: url(menu/bg1.jpg);"+
                "-fx-background-repeat: stretch;"+
                "-fx-background-position: center;"+
                "-fx-background-size: 120% 120%;");
        hBox.getChildren().addAll(vBox);
        hBox.setAlignment(Pos.CENTER);



        root.getChildren().add(hBox);
        stage.setScene(scene);
        stage.show();

    }

}
