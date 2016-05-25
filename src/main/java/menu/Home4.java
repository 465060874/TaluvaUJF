package menu;

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

    double size_ratio = 580.0 / 800.;
    int hauteurScene = 600;
    int largeurScene = (int) (hauteurScene * size_ratio);

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {

        //scene
        stage.setTitle("TALUVA_V3");
        Group root = new Group();
        Scene scene = new Scene(root, largeurScene, hauteurScene);
        //scene.setFill(Color.rgb(169, 234, 254));
        scene.getStylesheets().add("/menu/menus.css");

        int lv = largeurScene/2;

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



        //vBoxBas

        VBox vBoxsolo = new VBox();
        VBox vBoxmulti = new VBox();
        VBox vBoxcharger = new VBox();
        VBox vBoxnulle1 = new VBox();
        VBox vBoxnulle2 = new VBox();
        VBox vBoxnulle3 = new VBox();
        VBox vBoxgauche = new VBox();
        VBox vBoxmillieu = new VBox();
        VBox vBoxdroite = new VBox();

        vBoxsolo.setPrefHeight(hauteurScene*2/9);
        vBoxmulti.setPrefHeight(hauteurScene*2/9);
        vBoxcharger.setPrefHeight(hauteurScene*2/9);
        vBoxnulle1.setPrefHeight(hauteurScene*1/9);
        vBoxnulle2.setPrefHeight(hauteurScene*1/9);
        vBoxnulle3.setPrefHeight(hauteurScene*1/9);
        vBoxgauche.setPrefWidth(largeurScene/3-15);
        vBoxmillieu.setPrefWidth(largeurScene/3-15);
        vBoxdroite.setPrefWidth(largeurScene/3-15);

        vBoxgauche.getChildren().addAll(vBoxsolo,vBoxnulle1);
        vBoxmillieu.getChildren().addAll(vBoxnulle2,vBoxmulti);
        vBoxdroite.getChildren().addAll(vBoxcharger,vBoxnulle3);
        hBoxBas.getChildren().addAll(vBoxgauche,vBoxmillieu,vBoxdroite);

        Button bsolo = new Button("SOLO");
        Button bmulti = new Button("MULTI");
        Button bcharger = new Button("CHARGER");
        bsolo.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bmulti.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bcharger.setPrefSize(largeurScene/3,hauteurScene*2/9);

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

        vBoxsolo.getChildren().add(bsolo);
        vBoxmulti.getChildren().add(bmulti);
        vBoxcharger.getChildren().add(bcharger);
        /*
        //vBoxHaut : logo

        //hBoxMillieu

        //Button lancer
        Image lancer = new Image(getClass().getResourceAsStream("l.png"));
        Button blancer = new Button("", new ImageView(lancer));
        blancer.setPadding(new Insets(0, 0, 0, 0));
        Arc arc = new Arc();
        arc.setRadiusX(30.0f);
        arc.setRadiusY(30.0f);
        arc.setStartAngle(45.0f);
        arc.setLength(360.0f);
        arc.setType(ArcType.ROUND);
        blancer.setShape(arc);
        //Button redemarrrer;
        Image red = new Image(getClass().getResourceAsStream("red.png"));
        Button bred = new Button("", new ImageView(red));
        bred.setPadding(new Insets(0,0,0,0));
        //vBoxa : vide
        VBox vBoxa = new VBox();
        vBoxa.setPrefWidth(largeurScene*2/5);
        //vBoxb : Button lancer
        VBox vBoxb = new VBox();
        vBoxb.setAlignment(Pos.CENTER);
        vBoxb.setPrefWidth(largeurScene/5);
        //vBoxc : Button redemarrer
        VBox vBoxc = new VBox();
        vBoxc.setAlignment(Pos.CENTER_LEFT);
        vBoxc.setPrefWidth(largeurScene*2/5);






        vBoxb.getChildren().add(blancer);
        vBoxc.getChildren().add(bred);

        hBoxMillieu.getChildren().addAll(vBoxa,vBoxb,vBoxc);



        //vBoxBas
        //hBoxi : Button solo/multijoueurs/charger
        HBox hBoxi = new HBox();
        hBoxi.setAlignment(Pos.CENTER);
        hBoxi.setPrefWidth(largeurScene);
        hBoxi.setPrefHeight(hauteurScene/13);
        //hBoxc : StackPane
        HBox hBoxc = new HBox();
        hBoxc.setPrefHeight(hauteurScene/2-hauteurScene/13);
        hBoxc.setAlignment(Pos.CENTER);
        hBoxc.setPrefWidth(largeurScene);
        VBox vBoxbg = new VBox();
        int largeurBoder = (int) largeurScene/10;
        vBoxbg.setPrefSize(largeurScene-largeurBoder,50);





        hBoxc.getChildren().add(vBoxbg);
        vBoxbg.setAlignment(Pos.CENTER);
        VBox vBoxbgf = new VBox();
        vBoxbgf.setAlignment(Pos.CENTER);
        vBoxbg.getChildren().add(vBoxbgf);
        vBoxbgf.setPrefSize(largeurScene-largeurBoder,(hauteurScene/2)-(hauteurScene/6)-largeurBoder);
        vBoxBas.getChildren().addAll(hBoxc,hBoxi);










        Button solo= new Button("SOLO");
        Button multijoueurs= new Button("MULTIJOUEURS");
        Button charger= new Button("CHARGER");
        solo.setPrefWidth(largeurScene/3);
        multijoueurs.setPrefWidth(largeurScene/3);
        charger.setPrefWidth(largeurScene/3);
        solo.setPrefHeight(hauteurScene/10);
        multijoueurs.setPrefHeight(hauteurScene/10);
        charger.setPrefHeight(hauteurScene/10);
        hBoxi.getChildren().addAll(solo,multijoueurs,charger);

        StackPane stackPane = new StackPane();
        vBoxbgf.getChildren().add(stackPane);



        VBox vBoxSolo = new VBox(1);
        vBoxSolo.setAlignment(Pos.CENTER);
        VBox vBoxMultiJeurs = new VBox(5);
        vBoxMultiJeurs.setAlignment(Pos.CENTER);
        vBoxMultiJeurs.setPrefWidth(largeurScene);
        HBox hBoxCharger = new HBox();
        hBoxCharger.setAlignment(Pos.CENTER);
        hBoxCharger.setPrefWidth(largeurScene*2/3);


        //pane de button solo
        //deux buttons : icone /difficulte


        HBox hBoxI = new HBox(3);
        hBoxI.setAlignment(Pos.CENTER);
        HBox hBoxII = new HBox(3);
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
        vBoxSolo.getChildren().addAll(hBoxI,hBoxII);

        Label icone = new Label("VOTRE ICONE");
        Label niveaux = new Label("DIFFICULTE");
        icone.setPrefWidth(largeurScene/3);
        niveaux.setPrefWidth(largeurScene/3);
        icone.setAlignment(Pos.CENTER);
        niveaux.setAlignment(Pos.CENTER);
        vBoxg1.getChildren().add(icone);
        vBoxd1.getChildren().add(niveaux);

        Button bicone = new Button();
        Image imageDecline = new Image(getClass().getResourceAsStream("icone1.jpg"));
        bicone.setGraphic(new ImageView(imageDecline));
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

        solo.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(vBoxSolo);
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
        hBoxCharger.getChildren().add(sp);
        hBoxCharger.setPrefHeight(100);
        //hBoxCharger.setPadding(new Insets(20,0,30,0));
        charger.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(hBoxCharger);
        });




        vBoxMultiJeurs.getChildren().addAll(deuxjoueurs,troisjoueurs,quatrejoueurs1,quatrejoueurs2);
        multijoueurs.setOnAction(e -> {
            stackPane.getChildren().clear();
            stackPane.getChildren().add(vBoxMultiJeurs);
        });

*/



//CSS

        /*
        solo.getStyleClass().add("buttonniveaux4");
        multijoueurs.getStyleClass().add("buttonniveaux4");
        charger.getStyleClass().add("buttonniveaux4");
        icone.getStyleClass().add("bin");
        niveaux.getStyleClass().add("bin");
        simple.getStyleClass().add("buttonniveaux1");
        moyen.getStyleClass().add("buttonniveaux2");
        difficile.getStyleClass().add("buttonniveaux3");
        deuxjoueurs.getStyleClass().add("buttonjoueur");
        troisjoueurs.getStyleClass().add("buttonjoueur");
        quatrejoueurs1.getStyleClass().add("buttonjoueur");
        quatrejoueurs2.getStyleClass().add("buttonjoueur");*/
        //vBoxbgf.getStyleClass().add("b4");
        // stackPane.getStyleClass().add("b5");

        //hBoxi.getStyleClass().add("b2");
/*
        //CSS contour des box


        vBoxa.getStyleClass().add("b1");
        vBoxb.getStyleClass().add("b1");
        vBoxc.getStyleClass().add("b1");

        hBoxi.getStyleClass().add("b2");
        hBoxc.getStyleClass().add("b2");
        vBoxbg.getStyleClass().add("b1");


        hBoxI.getStyleClass().add("b3");
        hBoxII.getStyleClass().add("b3");
        vBoxg1.getStyleClass().add("b3");
        vBoxm1.getStyleClass().add("b3");
        vBoxd1.getStyleClass().add("b3");
        vBoxg2.getStyleClass().add("b1");
        vBoxm2.getStyleClass().add("b1");
        vBoxd2.getStyleClass().add("b1");
        //vBoxNiveaux.getStyleClass().add("b2");
        vBoxSolo.getStyleClass().add("b2");
        vBoxMultiJeurs.getStyleClass().add("b2");
        hBoxCharger.getStyleClass().add("b2");

*/
        vBoxScene.getStyleClass().add("svBoxScene");
        bsolo.getStyleClass().add("buttonniveaux4");
        bmulti.getStyleClass().add("buttonniveaux4");
        bcharger.getStyleClass().add("buttonniveaux4");



        vBoxScene.getStyleClass().add("b1");
        hBoxBas.getStyleClass().add("b3");
        vBoxHaut.getStyleClass().add("b1");
        hBoxMillieu.getStyleClass().add("b2");
        vBoxgauche.getStyleClass().add("b2");
        vBoxmillieu.getStyleClass().add("b2");
        vBoxdroite.getStyleClass().add("b2");
        vBoxsolo.getStyleClass().add("b1");
        vBoxmulti.getStyleClass().add("b1");
        vBoxcharger.getStyleClass().add("b1");
        vBoxnulle1.getStyleClass().add("b1");
        vBoxnulle2.getStyleClass().add("b1");
        vBoxnulle3.getStyleClass().add("b1");

        root.getChildren().add(vBoxScene);
        stage.setScene(scene);
        stage.show();

    }

}

