package menu;

import data.ChoosenColors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Created by zhaon on 25/05/16.
 */
public class Home4 extends Application {

    final String[] nomdimage = new String[]{"w.png", "r.png", "y.png","b.png"};
    final Image[] images = new Image[nomdimage.length];
    final ImageView[] pics = new ImageView[nomdimage.length];

    final String[] couleur = new String[]{
            ChoosenColors.WHITE.cssDefinition(),
            ChoosenColors.RED.cssDefinition(),
            ChoosenColors.YELLOW.cssDefinition(),
            ChoosenColors.BROWN.cssDefinition()
    };

    double size_ratio = 580.0 / 800.0;
    int hauteurScene = 600;
    int largeurScene = (int) (hauteurScene * size_ratio);
    int iterateurIcone = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        //System.out.println(ChoosenColors.BROWN);
        //scene
        stage.setTitle("TALUVA_V3");
        Group root = new Group();
        Scene scene = new Scene(root, largeurScene, hauteurScene);
        //scene.setFill(Color.rgb(169, 234, 254));
        scene.getStylesheets().add("/menu/menus.css");

        double lv = largeurScene/3;
        double hv = hauteurScene/3;

        VBox vBoxScene = new VBox();
        vBoxScene.setAlignment(Pos.CENTER);
        vBoxScene.setPrefSize(largeurScene,hauteurScene);


        VBox vBoxHaut = new VBox();
        vBoxHaut.setPrefSize(largeurScene,hauteurScene/3);
        HBox hBoxMillieu= new HBox(5);
        hBoxMillieu.setAlignment(Pos.CENTER);
        hBoxMillieu.setPrefSize(largeurScene,hauteurScene/3);
        HBox hBoxBas = new HBox(10);
        hBoxBas.setAlignment(Pos.CENTER);
        hBoxBas.setPrefSize(largeurScene,hauteurScene/3);


        vBoxScene.getChildren().addAll(vBoxHaut,hBoxMillieu,hBoxBas);

        // Tableau des images
        for (int i = 0; i < nomdimage.length; i++)
            images[i] = new Image(getClass().getResourceAsStream(nomdimage[i]));

        //vBoxBas

        VBox vBoxsolo = new VBox();
        VBox vBoxmulti = new VBox();
        VBox vBoxcharger = new VBox();
        VBox vBoxreprendre = new VBox();
        VBox vBoxnulle1 = new VBox();
        VBox vBoxnulle2 = new VBox();
        VBox vBoxnulle3 = new VBox();
        VBox vBoxgauche = new VBox();
        VBox vBoxmillieu = new VBox();
        VBox vBoxdroite = new VBox();

        vBoxreprendre.setAlignment(Pos.CENTER);
        vBoxnulle3.setAlignment(Pos.CENTER);

        vBoxsolo.setPrefHeight(lv-40);
        vBoxmulti.setPrefHeight(lv-40);
        vBoxcharger.setPrefHeight(lv-40);
        vBoxreprendre.setPrefSize(lv-50,lv-50);
        vBoxnulle1.setPrefHeight(hv*2/5);
        vBoxnulle2.setPrefHeight(hv*2/5);
        vBoxnulle3.setPrefHeight(hv*2/5);
        vBoxgauche.setPrefWidth(lv-40);
        vBoxmillieu.setPrefWidth(lv-40);
        vBoxdroite.setPrefWidth(lv-40);

        vBoxnulle3.getChildren().add(vBoxreprendre);
        vBoxgauche.getChildren().addAll(vBoxsolo,vBoxnulle1);
        vBoxmillieu.getChildren().addAll(vBoxnulle2,vBoxmulti);
        vBoxdroite.getChildren().addAll(vBoxcharger,vBoxnulle3);
        hBoxBas.getChildren().addAll(vBoxgauche,vBoxmillieu,vBoxdroite);

        Button bsolo = new Button("SOLO");
        Button bmulti = new Button("MULTI");
        Button bcharger = new Button("CHARGER");
        Button breprendre = new Button("->");
        bsolo.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bmulti.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bcharger.setPrefSize(largeurScene/3,hauteurScene*2/9);
        breprendre.setPrefSize(lv-100,lv-100);

        double[] path = new double[100];
        for (int q = 0; q < 24; q++) {
            double x = Math.cos(Math.PI / 3.0 * q + Math.PI / 2.0);
            double y = Math.sin(Math.PI / 3.0 * q + Math.PI / 2.0);
            path[q * 2] = x;
            path[q * 2 + 1] = y;
        }

        Polygon aPoly = new Polygon(path);
        bsolo.setShape(aPoly);
        bmulti.setShape(aPoly);
        bcharger.setShape(aPoly);
        breprendre.setShape(aPoly);
        vBoxsolo.getChildren().add(bsolo);
        vBoxmulti.getChildren().add(bmulti);
        vBoxcharger.getChildren().add(bcharger);
        vBoxreprendre.getChildren().add(breprendre);


        StackPane stackPane = new StackPane();
        hBoxMillieu.getChildren().add(stackPane);


        VBox vBoxS = new VBox(5);
        vBoxS.setAlignment(Pos.CENTER);
        VBox vBoxM = new VBox(5);
        vBoxM.setAlignment(Pos.CENTER);
        vBoxM.setPrefWidth(largeurScene);
        HBox hBoxC = new HBox();
        hBoxC.setAlignment(Pos.CENTER);
        hBoxC.setPrefWidth(largeurScene*2/3);
        hBoxC.setPadding(new Insets(10,10,10,10));


        //pane de button solo
        //deux buttons : icone /difficulte

        double largeurBoder = largeurScene/10;
        HBox hBoxI = new HBox(3);
        HBox hBoxII = new HBox(3);
        hBoxI.setAlignment(Pos.CENTER);
        hBoxII.setAlignment(Pos.CENTER);

        VBox vBoxg1 = new VBox();
        VBox vBoxm1 = new VBox();
        VBox vBoxd1 = new VBox();
        vBoxg1.setAlignment(Pos.CENTER);
        vBoxm1.setAlignment(Pos.CENTER);
        vBoxd1.setAlignment(Pos.CENTER);
        vBoxg1.setPrefWidth((largeurScene-25-largeurBoder)/2);
        vBoxm1.setPrefWidth(25);
        vBoxd1.setPrefWidth((largeurScene-25-largeurBoder)/2);

        VBox vBoxg2 = new VBox();
        VBox vBoxm2 = new VBox();
        VBox vBoxd2 = new VBox();
        vBoxg2.setAlignment(Pos.CENTER);
        vBoxd2.setAlignment(Pos.CENTER);
        vBoxm2.setAlignment(Pos.CENTER);
        vBoxg2.setPrefWidth((largeurScene-25-largeurBoder)/2);
        vBoxm2.setPrefWidth(25);
        vBoxd2.setPrefWidth((largeurScene-25-largeurBoder)/2);

        hBoxI.getChildren().addAll(vBoxg1,vBoxm1,vBoxd1);
        hBoxII.getChildren().addAll(vBoxg2,vBoxm2,vBoxd2);
        vBoxS.getChildren().addAll(hBoxI,hBoxII);

        Label icone = new Label("VOTRE ICONE");
        Label niveaux = new Label("DIFFICULTE");
        icone.setPrefWidth(largeurScene/3);
        niveaux.setPrefWidth(largeurScene/3);
        icone.setAlignment(Pos.CENTER);
        niveaux.setAlignment(Pos.CENTER);
        vBoxg1.getChildren().add(icone);
        vBoxd1.getChildren().add(niveaux);

        Button bicone = new Button();
        Image imageDecline = new Image(getClass().getResourceAsStream(nomdimage[1]));
        bicone.setPadding(new Insets(0,0,0,0));
        bicone.setGraphic(new ImageView(imageDecline));
        bicone.setStyle("-fx-background-color: " + couleur[1] +";");


        bicone.setPrefWidth(largeurScene/3);
        Image vs = new Image(getClass().getResourceAsStream("vs.png"));
        ImageView iv1 = new ImageView();
        iv1.setImage(vs);
        VBox vBoxNiveaux = new VBox(5);
        vBoxNiveaux.setAlignment(Pos.CENTER);
        Button simple = new Button("SIMPLE");
        Button moyen = new Button("MOYEN");
        Button difficile = new Button("DIFFICILE");
        simple.setPrefWidth(largeurScene/3);
        moyen.setPrefWidth(largeurScene/3);
        difficile.setPrefWidth(largeurScene/3);
        vBoxNiveaux.getChildren().addAll(simple,moyen,difficile);

        vBoxg2.getChildren().add(bicone);
        vBoxm2.getChildren().add(iv1);
        vBoxd2.getChildren().add(vBoxNiveaux);



        bsolo.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(vBoxS);
        });

        bicone.setOnAction(e->{
            if(iterateurIcone==nomdimage.length-1) {
                setIterateurIcone(0);
                bicone.setStyle("-fx-background-color: " + couleur[0] +";");
            }
            else if((iterateurIcone >= 0) && ( iterateurIcone < nomdimage.length)){
                setIterateurIcone(iterateurIcone + 1 % 4);
                bicone.setStyle("-fx-background-color: " + couleur[iterateurIcone] +";");
            }
            bicone.setGraphic(new ImageView(images[iterateurIcone]));

        });





        //pane button multijoueurs
        Button deuxjoueurs = new Button("2 JOUEURS");
        Button troisjoueurs = new Button("3 JOUEURS");
        Button quatrejoueurs1 = new Button("4 JOUEURS");
        Button quatrejoueurs2 = new Button("2 JOUEURS   VS  2 JOUEURS");
        deuxjoueurs.setPrefWidth(largeurScene/2);
        troisjoueurs.setPrefWidth(largeurScene/2);
        quatrejoueurs1.setPrefWidth(largeurScene/2);
        quatrejoueurs2.setPrefWidth(largeurScene/2);

        //pane charger
        ScrollPane sp = new ScrollPane();
        Rectangle rec1 =  new Rectangle(largeurScene*2/3, 50);
        Rectangle rec2 =  new Rectangle(largeurScene*2/3, 50);
        Rectangle rec3 =  new Rectangle(largeurScene*2/3, 50);
        Rectangle rec4 =  new Rectangle(largeurScene*2/3, 50);
        Rectangle rec5 =  new Rectangle(largeurScene*2/3, 50);
        Rectangle rec6 =  new Rectangle(largeurScene*2/3, 50);
        Rectangle rec7 =  new Rectangle(largeurScene*2/3, 50);
        VBox vBoxRect = new VBox(2);
        vBoxRect.setPrefHeight(100);
        vBoxRect.setAlignment(Pos.CENTER);
        vBoxRect.getChildren().addAll(rec1,rec2,rec3,rec4,rec5,rec6,rec7);
        sp.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setContent(vBoxRect);
        hBoxC.getChildren().add(sp);
        hBoxC.setPrefHeight(100);
        //hBoxCharger.setPadding(new Insets(20,0,30,0));
        bcharger.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(hBoxC);
        });




        vBoxM.getChildren().addAll(deuxjoueurs,troisjoueurs,quatrejoueurs1,quatrejoueurs2);
        bmulti.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(vBoxM);
        });





        vBoxScene.getStyleClass().add("svBoxScene");
        bsolo.getStyleClass().add("buttonniveaux4");
        bmulti.getStyleClass().add("buttonniveaux4");
        bcharger.getStyleClass().add("buttonniveaux4");
        breprendre.getStyleClass().add("buttonniveaux5");

        icone.getStyleClass().add("bin");
        niveaux.getStyleClass().add("bin");
        simple.getStyleClass().add("buttonniveaux1");
        moyen.getStyleClass().add("buttonniveaux2");
        difficile.getStyleClass().add("buttonniveaux3");
        deuxjoueurs.getStyleClass().add("buttonjoueur");
        troisjoueurs.getStyleClass().add("buttonjoueur");
        quatrejoueurs1.getStyleClass().add("buttonjoueur");
        quatrejoueurs2.getStyleClass().add("buttonjoueur");

/*
        vBoxScene.getStyleClass().add("b1");
        hBoxBas.getStyleClass().add("b3");
        vBoxHaut.getStyleClass().add("b1");
        //hBoxMillieu.getStyleClass().add("b2");
        vBoxgauche.getStyleClass().add("b2");
        vBoxmillieu.getStyleClass().add("b2");
        vBoxdroite.getStyleClass().add("b2");
        vBoxsolo.getStyleClass().add("b1");
        vBoxmulti.getStyleClass().add("b1");
        vBoxcharger.getStyleClass().add("b1");
        vBoxnulle1.getStyleClass().add("b1");
        vBoxnulle2.getStyleClass().add("b1");
        vBoxnulle3.getStyleClass().add("b1");
*/
        root.getChildren().add(vBoxScene);
        stage.setScene(scene);
        stage.show();

    }

    private void setIterateurIcone( int val ){
        iterateurIcone = val;
    }

}

