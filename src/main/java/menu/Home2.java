package menu;


import com.sun.javafx.scene.control.skin.LabeledImpl;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
public class Home2 extends Application{

    double size_ratio = 580.0 / 800.;
    int hauteurs = 600;
    int largeurs = (int) (hauteurs * size_ratio);

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {

        //scene
        stage.setTitle("TALUVA");
        Group root = new Group();
        Scene scene = new Scene(root, largeurs, hauteurs);
        //scene.setFill(Color.rgb(169, 234, 254));
        scene.getStylesheets().add("/menu/menus.css");


        int lv = largeurs/2;



        VBox vBoxScene = new VBox();
        vBoxScene.setAlignment(Pos.CENTER);
        vBoxScene.setPrefSize(largeurs,hauteurs);
        //vBox.setStyle("-fx-border-color: pink;" +
        //"-fx-border-style: solid;-fx-border-width: 1;");

        VBox vBoxHaut = new VBox();
        vBoxHaut.setPrefSize(largeurs,hauteurs/3);
        //vBoxnull.setStyle("-fx-border-color: pink;" +
                //"-fx-border-style: solid;-fx-border-width: 1;");
        HBox hBoxMillieu= new HBox(5);
        hBoxMillieu.setAlignment(Pos.CENTER);
        hBoxMillieu.setPrefSize(largeurs,hauteurs/6);
        //vBoxb.setStyle("-fx-border-color: pink;" +
         //       "-fx-border-style: solid;-fx-border-width: 1;");
        VBox vBoxBas = new VBox();
        vBoxBas.setAlignment(Pos.CENTER);
        vBoxBas.setPrefSize(largeurs,hauteurs/2);
       // vBoxp.setStyle("-fx-border-color: pink;" +
        //                 "-fx-border-style: solid;-fx-border-width: 1;");

        vBoxScene.getChildren().addAll(vBoxHaut,hBoxMillieu,vBoxBas);
        vBoxScene.getStyleClass().add("d");
        /*
        //boutton démerrer

        Image lancer = new Image(getClass().getResourceAsStream("l.png"));
        Button blancer = new Button("", new ImageView(lancer));
        blancer.setPadding(new Insets(0, 0, 0, 0));

        Arc arc = new Arc();
       // arc.setCenterX(40.0f);
       // arc.setCenterY(40.0f);
        arc.setRadiusX(30.0f);
        arc.setRadiusY(30.0f);
        arc.setStartAngle(45.0f);
        arc.setLength(360.0f);
        arc.setType(ArcType.ROUND);

        blancer.setShape(arc);

        Image red = new Image(getClass().getResourceAsStream("red.png"));
        Button bred = new Button("", new ImageView(red));
        bred.setPadding(new Insets(0,0,0,0));

        VBox vBoxa = new VBox();
        VBox vBoxb = new VBox();
        vBoxb.setAlignment(Pos.CENTER);
        VBox vBoxc = new VBox();
        vBoxc.setAlignment(Pos.CENTER_LEFT);
/*
        vBoxa.setPrefWidth(largeurs/3);
        vBoxb.setPrefWidth(largeurs/3);
        vBoxc.setPrefWidth(largeurs/3);
*/
        /*
        vBoxa.setPrefWidth(largeurs*2/5);
        vBoxb.setPrefWidth(largeurs/5);
        vBoxc.setPrefWidth(largeurs*2/5);

        vBoxb.getChildren().add(blancer);
        vBoxc.getChildren().add(bred);

        hBoxb.getChildren().addAll(vBoxa,vBoxb,vBoxc);


        HBox hBoxi = new HBox(10);
        hBoxi.setAlignment(Pos.CENTER);
        hBoxi.setPrefWidth(largeurs);
       // hBoxi.setStyle("-fx-border-color: pink;" +
       //         "-fx-border-style: solid;-fx-border-width: 1;");
        HBox hBoxc = new HBox();
        hBoxc.setPrefHeight(vBoxp.getPrefHeight()-50);
        hBoxc.setAlignment(Pos.CENTER);
        hBoxc.setPrefWidth(largeurs);
        //hBoxc.setStyle("-fx-border-color: pink;" +
        //        "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxp.getChildren().addAll(hBoxc,hBoxi);

        Button solo= new Button("SOLO");

        solo.setPrefWidth(largeurs/8);
        Button multijoueurs= new Button("MULTIJOUEURS");
        multijoueurs.setPrefWidth(largeurs/8);
        Button charger= new Button("CHARGER");
        charger.setPrefWidth(largeurs/8);
        hBoxi.getChildren().addAll(solo,multijoueurs,charger);
        //System.out.println(multijoueurs.getWidth());
        //System.out.println(solo.getWidth());

        StackPane stackPane = new StackPane();
        hBoxc.getChildren().add(stackPane);

        solo.getStyleClass().add("buttonniveaux4");
        multijoueurs.getStyleClass().add("buttonniveaux4");
        charger.getStyleClass().add("buttonniveaux4");
        */
        /*
        //tabpane
        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.BOTTOM);
        System.out.println(tabPane.getStyleClass());
        BorderPane mainPane = new BorderPane();
*/

        /*
        Tab tabA = new Tab();

        tabA.setClosable(false);
        tabPane.getTabs().add(tabA);
       // tabA.setStyle("-fx-background-color: pink;");

        Tab tabB = new Tab();

        tabB.setClosable(false);
        tabPane.getTabs().add(tabB);
        //tabB.setStyle("-fx-background-color: pink;");

        Tab tabC = new Tab();

        tabA.setText("SOLO");
        tabB.setText("MULTI JOUEURS");
        tabC.setText("CHARGER");
        tabC.setClosable(false);
        tabPane.getTabs().add(tabC);
        //tabC.setStyle("-fx-background-color: pink;");

        tabPane.setSide(Side.TOP);
        //tabA.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icone1.jpg"))));
        //tabB.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icone1.jpg"))));
        //tabC.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icone1.jpg"))));
*/
        /*
        VBox vBoxg = new VBox(1);
        vBoxg.setPrefWidth(lv);
        vBoxg.setAlignment(Pos.CENTER);
        //
        //vBoxg.setStyle("-fx-border-color: pink;" +
        //        "-fx-border-style: solid;-fx-border-width: 1;");
        HBox hBoxg = new HBox(2);
        hBoxg.setPrefWidth(lv);
        hBoxg.setAlignment(Pos.CENTER);
       // hBoxg.setStyle("-fx-border-color: pink;" +
        //       "-fx-border-style: solid;-fx-border-width: 1;");
        HBox hBoxh = new HBox(2);
        //hBoxh.setStyle("-fx-border-color: pink;" +
          //      "-fx-border-style: solid;-fx-border-width: 1;");
        hBoxh.setAlignment(Pos.CENTER);
        VBox vBoxhg = new VBox();
        //vBoxhg.setStyle("-fx-border-color: pink;" +
        //        "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxhg.setPrefWidth(lv/2);
        vBoxhg.setAlignment(Pos.CENTER);
        Label icone = new Label("VOTRE ICONE");
        icone.setAlignment(Pos.CENTER);
        icone.setPrefWidth(lv/2);
        icone.setStyle("-fx-background-color:#B9121B;"+
                       "-fx-text-fill: white;");
        vBoxhg.getChildren().add(icone);
        VBox vBoxhd = new VBox();
        //vBoxhd.setStyle("-fx-border-color: pink;" +
          //      "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxhd.setPrefWidth(lv/2);
        vBoxhd.setAlignment(Pos.CENTER);
        Label niveaux = new Label("DIFFICULTE");
        niveaux.setAlignment(Pos.CENTER);
        niveaux.setPrefWidth(lv/2);
        niveaux.setStyle("-fx-background-color:#B9121B;"+
                "-fx-text-fill: white;");
        vBoxhd.getChildren().add(niveaux);
        VBox vBoxm  = new VBox();
        vBoxm.setPrefWidth(30);
        hBoxh.getChildren().addAll(vBoxhg,vBoxm,vBoxhd);
        vBoxg.getChildren().add(hBoxh);


        VBox vBoxgg = new VBox(5);
        // vBoxgg.setStyle("-fx-border-color: pink;" +
        //       "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxgg.setPadding(new Insets(10, 10, 10, 10));
        vBoxgg.setPrefWidth(lv/2);
        vBoxgg.setAlignment(Pos.CENTER);


        Button bicone = new Button();
        bicone.setPrefSize(lv/2,100);
        Image imageDecline = new Image(getClass().getResourceAsStream("icone1.jpg"));
        bicone.setGraphic(new ImageView(imageDecline));
        vBoxgg.getChildren().addAll(bicone);
        //vBoxgg.setStyle("-fx-border-color: pink;" +
        //        "-fx-border-style: solid;-fx-border-width: 1;");
        VBox vBoxgd = new VBox(5);

        vBoxgd.setPadding(new Insets(10, 10, 10, 10));
        vBoxgd.setPrefWidth(lv/2);
       // vBoxgd.setStyle("-fx-border-color: pink;" +
       //         "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxgd.setAlignment(Pos.CENTER);
        ToggleButton simple = new ToggleButton("SIMPLE");
        simple.setPrefWidth(lv/2);
        ToggleButton moyen = new ToggleButton("MOYEN");
        moyen.setPrefWidth(lv/2);
        ToggleButton difficile = new ToggleButton("DIFFICILE");
        difficile.setPrefWidth(lv/2);

        vBoxgd.getChildren().addAll(simple,moyen,difficile);

        simple.getStyleClass().add("buttonniveaux1");
        moyen.getStyleClass().add("buttonniveaux2");
        difficile.getStyleClass().add("buttonniveaux3");

        VBox vBoxm2 = new VBox();
        vBoxm2.setPrefWidth(30);
        vBoxm2.setAlignment(Pos.CENTER);
        Image vs = new Image(getClass().getResourceAsStream("vs.png"));
        ImageView iv1 = new ImageView();
        iv1.setImage(vs);
        vBoxm2.getChildren().add(iv1);

        hBoxg.getChildren().addAll(vBoxgg,vBoxm2,vBoxgd);
        vBoxg.getChildren().add(hBoxg);
        solo.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(hBoxg);
        });
      //  tabA.setContent(vBoxg);

        VBox vBox2 = new VBox(5);
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setPrefWidth(largeurs);
        //Button unjoueur = new Button("[JOUEUR1]");
        //unjoueur.setPrefWidth(lv);
        Button deuxjoueurs = new Button("2 JOUEURS");
        deuxjoueurs.setPrefWidth(lv/2);
        Button troisjoueurs = new Button("3 JOUEURS");
        troisjoueurs.setPrefWidth(lv/2);
        Button quatrejoueurs1 = new Button("4 JOUEURS");
        quatrejoueurs1.setPrefWidth(lv/2);
        Button quatrejoueurs2 = new Button("2 JOUEURS   VS  2 JOUEURS");
        quatrejoueurs2.setPrefWidth(lv/2);

       // unjoueur.getStyleClass().add("buttonjoueur");
        deuxjoueurs.getStyleClass().add("buttonjoueur");
        troisjoueurs.getStyleClass().add("buttonjoueur");
        quatrejoueurs1.getStyleClass().add("buttonjoueur");
        quatrejoueurs2.getStyleClass().add("buttonjoueur");



        vBox2.getChildren().addAll(deuxjoueurs,troisjoueurs,quatrejoueurs1,quatrejoueurs2);
        multijoueurs.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(vBox2);
        });
        //tabB.setContent(vBox2);
        ScrollPane sp = new ScrollPane();
        //sp.setPrefHeight(100);

        //ScrollBar sc = new ScrollBar();
        Rectangle rec1 =  new Rectangle(lv, 50);
        Rectangle rec2 =  new Rectangle(lv, 50);
        Rectangle rec3 =  new Rectangle(lv, 50);
        Rectangle rec4 =  new Rectangle(lv, 50);
        Rectangle rec5 =  new Rectangle(lv, 50);
        Rectangle rec6 =  new Rectangle(lv, 50);
        Rectangle rec7 =  new Rectangle(lv, 50);

        rec1.setStyle("-fx-background-color: pink;"+
                "-fx-text-fill: white;");
        VBox vb = new VBox(2);
        vb.setPrefHeight(100);
        vb.setAlignment(Pos.CENTER);
        //vb.setPrefWidth(lv );
        //vb.getChildren().addAll(new Label("Choisir votre partie"));
        vb.getChildren().addAll(rec1,rec2,rec3,rec4,rec5,rec6,rec7);
        //vb.getChildren().addAll(rec1,rec2,rec3);
        //tabC.setContent(sp);
        sp.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setContent(vb);

        HBox hb1 = new HBox();
        hb1.setAlignment(Pos.CENTER);
        hb1.getChildren().addAll(sp);
        hb1.setPrefHeight(100);
        hb1.setPadding(new Insets(20,0,30,0));
        charger.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(hb1);
        });



        //tabC.setContent(hb1);
        //DropShadow shadow = new DropShadow();
*/

        //tabC.setContent();
/*
        mainPane.setCenter(tabPane);

        mainPane.prefHeightProperty().bind(vBoxtab.heightProperty());
        mainPane.prefWidthProperty().bind(vBoxtab.widthProperty());
        vBoxtab.getChildren().add(mainPane);


        */



        vBoxScene.setStyle("-fx-background-image: url(menu/bg2.jpg);"+
                "-fx-background-repeat: stretch;"+
                "-fx-background-position: top;"+
                "-fx-background-size: 100% 100%;");

        root.getChildren().add(vBoxScene);
        stage.setScene(scene);
        stage.show();

    }

}
