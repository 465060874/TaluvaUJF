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

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(largeurs,hauteurs);

        VBox vBoxnull = new VBox();
        vBoxnull.setPrefSize(largeurs,hauteurs/3);
        //vBoxnull.setStyle("-fx-border-color: pink;" +
                //"-fx-border-style: solid;-fx-border-width: 1;");
        HBox hBoxb= new HBox(5);
        hBoxb.setAlignment(Pos.CENTER);
        hBoxb.setPrefSize(largeurs,hauteurs/6);
        //vBoxb.setStyle("-fx-border-color: pink;" +
         //       "-fx-border-style: solid;-fx-border-width: 1;");
        VBox vBoxtab = new VBox();
        vBoxtab.setAlignment(Pos.CENTER);
        vBoxtab.setPrefSize(largeurs,hauteurs/2);
        //vBoxtab.setStyle("-fx-border-color: pink;" +
         //       "-fx-border-style: solid;-fx-border-width: 1;");

        vBox.getChildren().addAll(vBoxnull,hBoxb,vBoxtab);

        //boutton démerrer

        Image lancer = new Image(getClass().getResourceAsStream("d.png"));
        Button blancer = new Button("", new ImageView(lancer));
        Image red = new Image(getClass().getResourceAsStream("red.png"));
        Button bred = new Button("", new ImageView(red));

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
        vBoxa.setPrefWidth(largeurs*2/5);
        vBoxb.setPrefWidth(largeurs/5);
        vBoxc.setPrefWidth(largeurs*2/5);

        vBoxb.getChildren().add(blancer);
        vBoxc.getChildren().add(bred);

        hBoxb.getChildren().addAll(vBoxa,vBoxb,vBoxc);

        //tabpane
        TabPane tabPane = new TabPane();
        BorderPane mainPane = new BorderPane();

        //Create Tabs
        Tab tabnulle = new Tab();
        tabnulle.setText("                                                                                         ");
        tabnulle.setClosable(false);
        tabPane.getTabs().add(tabnulle);
        tabnulle.setStyle("-fx-background-color: transparent;");

        Tab tabA = new Tab();
        tabA.setText("SOLO");
        tabA.setClosable(false);
        tabPane.getTabs().add(tabA);
        tabA.setStyle("-fx-background-color: transparent;");

        Tab tabB = new Tab();
        tabB.setText("MULTI JOUEURS");
        tabB.setClosable(false);
        tabPane.getTabs().add(tabB);
        tabB.setStyle("-fx-background-color: transparent;");

        Tab tabC = new Tab();
        tabC.setText("CHARGER");
        tabC.setClosable(false);
        tabPane.getTabs().add(tabC);
        tabC.setStyle("-fx-background-color: transparent;");

        tabPane.setSide(Side.BOTTOM);
        //tabA.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icone1.jpg"))));
        //tabB.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icone1.jpg"))));
        //tabC.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icone1.jpg"))));

        VBox vBoxg = new VBox(1);
        vBoxg.setPrefWidth(lv);
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
         //       "-fx-border-style: solid;-fx-border-width: 1;");
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
        bicone.setPrefSize(lv/4,100);
        Image imageDecline = new Image(getClass().getResourceAsStream("icone1.jpg"));
        bicone.setGraphic(new ImageView(imageDecline));
        vBoxgg.getChildren().addAll(bicone);
        vBoxgg.setStyle("-fx-border-color: pink;" +
                "-fx-border-style: solid;-fx-border-width: 1;");
        VBox vBoxgd = new VBox(5);

        vBoxgd.setPadding(new Insets(10, 10, 10, 10));
        vBoxgd.setPrefWidth(lv/2);
        vBoxgd.setStyle("-fx-border-color: pink;" +
                "-fx-border-style: solid;-fx-border-width: 1;");
        vBoxgd.setAlignment(Pos.CENTER);
        ToggleButton simple = new ToggleButton("SIMPLE");
        simple.setPrefWidth(lv/4);
        ToggleButton moyen = new ToggleButton("MOYEN");
        moyen.setPrefWidth(lv/4);
        ToggleButton difficile = new ToggleButton("DIFFICILE");
        difficile.setPrefWidth(lv/4);

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
        tabA.setContent(vBoxg);

        VBox vBox2 = new VBox(5);
        vBox2.setAlignment(Pos.CENTER);
        vBox2.setPrefWidth(lv);
        //Button unjoueur = new Button("[JOUEUR1]");
        //unjoueur.setPrefWidth(lv);
        Button deuxjoueurs = new Button("[JOUEUR1]  [JOUEUR2]");
        deuxjoueurs.setPrefWidth(lv);
        Button troisjoueurs = new Button("[JOUEUR1]  [JOUEUR2]  [JOUEUR3]");
        troisjoueurs.setPrefWidth(lv);
        Button quatrejoueurs1 = new Button("[JOUEUR1]  [JOUEUR2]  [JOUEUR3]  [JOUEUR4]");
        quatrejoueurs1.setPrefWidth(lv);
        Button quatrejoueurs2 = new Button("[JOUEUR1 / JOUEUR2]     [JOUEUR3 / JOUEUR4]");
        quatrejoueurs2.setPrefWidth(lv);

       // unjoueur.getStyleClass().add("buttonjoueur");
        deuxjoueurs.getStyleClass().add("buttonjoueur");
        troisjoueurs.getStyleClass().add("buttonjoueur");
        quatrejoueurs1.getStyleClass().add("buttonjoueur");
        quatrejoueurs2.getStyleClass().add("buttonjoueur");



        vBox2.getChildren().addAll(deuxjoueurs,troisjoueurs,quatrejoueurs1,quatrejoueurs2);
        tabB.setContent(vBox2);
        ScrollPane sp = new ScrollPane();
        //sp.setPrefHeight(100);

        //ScrollBar sc = new ScrollBar();
        Rectangle rec1 =  new Rectangle(lv/2, 50);
        Rectangle rec2 =  new Rectangle(lv/2, 50);
        Rectangle rec3 =  new Rectangle(lv/2, 50);
        Rectangle rec4 =  new Rectangle(lv/2, 50);
        Rectangle rec5 =  new Rectangle(lv/2, 50);
        Rectangle rec6 =  new Rectangle(lv/2, 50);
        Rectangle rec7 =  new Rectangle(lv/2, 50);

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
        tabC.setContent(hb1);
        //DropShadow shadow = new DropShadow();


        //tabC.setContent();

        mainPane.setCenter(tabPane);

        mainPane.prefHeightProperty().bind(vBoxtab.heightProperty());
        mainPane.prefWidthProperty().bind(vBoxtab.widthProperty());
        vBoxtab.getChildren().add(mainPane);
        vBox.setStyle("-fx-background-image: url(menu/bg1.jpg);"+
                "-fx-background-repeat: stretch;"+
                "-fx-background-position: center;"+
                "-fx-background-size: 120% 120%;");

        root.getChildren().add(vBox);
        stage.setScene(scene);
        stage.show();

    }

}
