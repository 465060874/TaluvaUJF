package menu;

import IA.IADifficulty;
import data.ChoosenColors;
import data.PlayerColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import menu.data.MenuData;
import menu.data.MenuMode;
import menu.data.MultiMode;
import ui.GameApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaon on 25/05/16.
 */
public class Home4 extends Application {

    static final String[] nomdimage = new String[]{"w.png", "r.png", "y.png","b.png"};
    static final Image[] images = new Image[nomdimage.length];
    static final ImageView[] pics = new ImageView[nomdimage.length];
    static final String[] couleur = new String[]{
            ChoosenColors.WHITE.cssDefinition(),
            ChoosenColors.RED.cssDefinition(),
            ChoosenColors.YELLOW.cssDefinition(),
            ChoosenColors.BROWN.cssDefinition()
    };

    static double size_ratio = 580.0 / 800.0;
    static int hauteurScene = 600;
    static int largeurScene = (int) (hauteurScene * size_ratio);

    public static void main(String[] args) {
        launch(args);
    }

    private final MenuData menuData;

    private int iterateurIcone = 0;

    private Stage stage;

    private ToggleGroup tabChoice;
    private ToggleButton[] optionsButton;
    private StackPane options;
    private Node[] optionsContent;

    private ToggleGroup levelChoice;
    private ToggleButton[] levelButtons;

    private ToggleGroup mode;
    private ToggleButton[] optionsMode;

    private ToggleGroup historyChoice;
    private List<ToggleButton> optionsHistory;
    private List<Node> capture;
    private VBox vBoxCapture = new VBox();

    public Home4() {
        this.menuData = MenuData.load();
    }

    @Override public void start(Stage stage) {
        this.stage = stage;
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
        vBoxHaut.setAlignment(Pos.BOTTOM_CENTER);
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



        //vBoxHaut
        Image vs1 = new Image(getClass().getResourceAsStream("fb.png"));
        ImageView iv2 = new ImageView();
        iv2.setImage(vs1);

        vBoxHaut.getChildren().add(iv2);

        //vBoxBas

        VBox vBoxsolo = new VBox();
        VBox vBoxmulti = new VBox();
        VBox vBoxcharger = new VBox();
        VBox vBoxreprendre = new VBox();
        VBox vBoxq = new VBox();
        VBox vBoxnulle1 = new VBox();
        VBox vBoxnulle2 = new VBox();
        VBox vBoxnulle3 = new VBox();
        VBox vBoxgauche = new VBox();
        VBox vBoxmillieu = new VBox();
        VBox vBoxdroite = new VBox();

        vBoxreprendre.setAlignment(Pos.CENTER);
        vBoxnulle3.setAlignment(Pos.CENTER);
        vBoxq.setAlignment(Pos.CENTER);
        vBoxsolo.setPrefHeight(lv-40);
        vBoxmulti.setPrefHeight(lv-40);
        vBoxcharger.setPrefHeight(lv-40);
        vBoxreprendre.setPrefSize(lv-50,lv-50);
        vBoxq.setPrefSize(lv-50,lv-50);

        vBoxnulle1.setPrefHeight(hv*2/5);
        vBoxnulle2.setPrefHeight(hv*2/5);
        vBoxnulle3.setPrefHeight(hv*2/5);
        vBoxgauche.setPrefWidth(lv-40);
        vBoxmillieu.setPrefWidth(lv-40);
        vBoxdroite.setPrefWidth(lv-40);

        vBoxnulle2.getChildren().add(vBoxreprendre);
        vBoxnulle3.getChildren().add(vBoxq);
        vBoxgauche.getChildren().addAll(vBoxsolo,vBoxnulle1);
        vBoxmillieu.getChildren().addAll(vBoxnulle2,vBoxmulti);
        vBoxdroite.getChildren().addAll(vBoxcharger,vBoxnulle3);
        hBoxBas.getChildren().addAll(vBoxgauche,vBoxmillieu,vBoxdroite);

        ToggleButton bsolo = new ToggleButton("SOLO");
        ToggleButton bmulti = new ToggleButton("MULTI");
        ToggleButton bcharger = new ToggleButton("CHARGER");
        ToggleButton breprendre = new ToggleButton("GO");
        ToggleButton bq = new ToggleButton("Q");
        bsolo.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bmulti.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bcharger.setPrefSize(largeurScene/3,hauteurScene*2/9);
        breprendre.setPrefSize(lv-100,lv-100);
        bq.setPrefSize(lv-100,lv-100);

        this.optionsButton = new ToggleButton[] { bsolo, bmulti, bcharger };


        this.tabChoice = new ToggleGroup();
        bsolo.setToggleGroup(tabChoice);
        bmulti.setToggleGroup(tabChoice);
        bcharger.setToggleGroup(tabChoice);
        tabChoice.selectToggle(bsolo);
        tabChoice.selectedToggleProperty().addListener(e -> updateMode());

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
        bq.setShape(aPoly);
        vBoxsolo.getChildren().add(bsolo);
        vBoxmulti.getChildren().add(bmulti);
        vBoxcharger.getChildren().add(bcharger);
        vBoxreprendre.getChildren().add(breprendre);
        vBoxq.getChildren().add(bq);


        this.options = new StackPane();
        hBoxMillieu.getChildren().add(options);


        VBox soloOptions = new VBox(5);
        soloOptions.setAlignment(Pos.CENTER);
        VBox multiOptions = new VBox(5);
        multiOptions.setAlignment(Pos.CENTER);
        multiOptions.setPrefWidth(largeurScene);
        HBox chargerList = new HBox(30);
        chargerList.setAlignment(Pos.CENTER);
        chargerList.setPrefWidth(largeurScene*2/3);
        chargerList.setPadding(new Insets(10,10,10,10));
        this.optionsContent = new Node[] { soloOptions, multiOptions, chargerList };




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
        soloOptions.getChildren().addAll(hBoxI,hBoxII);

        Label icone = new Label("ICONE");
        Label niveaux = new Label("DIFFICULTE");
        icone.setPrefWidth(largeurScene/3);
        icone.setPrefHeight(27);
        niveaux.setPrefWidth(largeurScene/3);
        niveaux.setPrefHeight(27);
        icone.setAlignment(Pos.CENTER);
        niveaux.setAlignment(Pos.CENTER);
        vBoxg1.getChildren().add(icone);
        vBoxd1.getChildren().add(niveaux);

        Button bicone = new Button();
        Image imageDecline = new Image(getClass().getResourceAsStream(nomdimage[menuData.getSoloColor().ordinal()]));
        bicone.setPadding(new Insets(0,0,0,0));
        bicone.setGraphic(new ImageView(imageDecline));
        bicone.setStyle("-fx-background-color: " + couleur[menuData.getSoloColor().ordinal()] +";");


        bicone.setPrefWidth(largeurScene/3);
        Image vs = new Image(getClass().getResourceAsStream("vs.png"));
        ImageView iv1 = new ImageView();
        iv1.setImage(vs);
        VBox vBoxNiveaux = new VBox(5);
        vBoxNiveaux.setAlignment(Pos.CENTER);
        ToggleButton simple = new ToggleButton("SIMPLE");
        ToggleButton moyen = new ToggleButton("MOYEN");
        ToggleButton difficile = new ToggleButton("DIFFICILE");

        this.levelButtons = new ToggleButton[] { simple, moyen, difficile };
        this.levelChoice = new ToggleGroup();
        simple.setToggleGroup(levelChoice);
        moyen.setToggleGroup(levelChoice);
        difficile.setToggleGroup(levelChoice);
        levelChoice.selectToggle(levelButtons[menuData.getSoloDifficulty().ordinal()]);
        levelChoice.selectedToggleProperty().addListener(e -> updateLevel());

        simple.setPrefWidth(largeurScene/3);
        moyen.setPrefWidth(largeurScene/3);
        difficile.setPrefWidth(largeurScene/3);
        vBoxNiveaux.getChildren().addAll(simple,moyen,difficile);

        vBoxg2.getChildren().add(bicone);
        vBoxm2.getChildren().add(iv1);
        vBoxd2.getChildren().add(vBoxNiveaux);









        //pane button multijoueurs
        ToggleButton md = new ToggleButton("2 JOUEURS");
        ToggleButton mt = new ToggleButton("3 JOUEURS");
        ToggleButton mq1 = new ToggleButton("4 JOUEURS");
        ToggleButton mq2 = new ToggleButton("2 JOUEURS   VS  2 JOUEURS");

        this.optionsMode = new ToggleButton[] { md,mt,mq1,mq2 };
        this.mode = new ToggleGroup();
        md.setToggleGroup(mode);
        mt.setToggleGroup(mode);
        mq1.setToggleGroup(mode);
        mq2.setToggleGroup(mode);
        mode.selectToggle(optionsMode[menuData.getMultiMode().ordinal()]);
        mode.selectedToggleProperty().addListener(e -> updatemultimode());


        md.setPrefWidth(largeurScene/2);
        mt.setPrefWidth(largeurScene/2);
        mq1.setPrefWidth(largeurScene/2);
        mq2.setPrefWidth(largeurScene/2);
        multiOptions.getChildren().addAll(md,mt,mq1,mq2);

        //pane charger
        int kkk = 170;
        Rectangle carre1 = new Rectangle(kkk,kkk, Color.AZURE);
        Rectangle carre2 = new Rectangle(kkk,kkk, Color.DARKBLUE);
        Rectangle carre3 = new Rectangle(kkk,kkk, Color.BLUEVIOLET);
        this.capture = new ArrayList<Node>();
        for(int i = 0;i< 3; i++){
            for(int j = 0;j<3;j++){
                capture.add(carre1);
                capture.add(carre2);
                capture.add(carre3);
            }
        }



        VBox vBoxoptionsCharger = new VBox(8);

        chargerList.setAlignment(Pos.CENTER);
        vBoxCapture.setAlignment(Pos.CENTER);
        vBoxoptionsCharger.setAlignment(Pos.CENTER);
        chargerList.setPrefWidth(largeurScene);
        vBoxoptionsCharger.setPrefWidth(150);
        vBoxoptionsCharger.setPrefHeight(kkk);


        //vBoxCapture.setPrefHeight(100);
        //vBoxoptionsCharger.setPrefHeight(100);

        this.optionsHistory = new ArrayList<ToggleButton>();
        this.historyChoice = new ToggleGroup();
        for (int i = 1; i < 10;i++) {
;           ToggleButton num = new ToggleButton("NUM " + i);
            num.setPrefWidth(120);
            optionsHistory.add(num);
            num.setToggleGroup(historyChoice);

        }
        historyChoice.selectToggle(optionsHistory.get(2));
        historyChoice.selectedToggleProperty().addListener(e -> updateCapture());

        vBoxoptionsCharger.getChildren().addAll(optionsHistory);
        ScrollPane sp = new ScrollPane();
        VBox v = new VBox();
        v.getChildren().add(sp);
        v.setPrefSize(150,100);

        sp.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
        vBoxoptionsCharger.setAlignment(Pos.CENTER_LEFT);
        vBoxoptionsCharger.setPadding(new Insets(2,1,0,1));
        sp.setContent(vBoxoptionsCharger);
        vBoxCapture.getChildren().add(carre1);

        chargerList.getChildren().addAll(vBoxCapture,v);
        chargerList.setPrefHeight(80);

        v.setAlignment(Pos.CENTER);
        vBoxCapture.setAlignment(Pos.CENTER);
        //vBoxCapture.getStyleClass().add("b2");
        //v.getStyleClass().add("b2");







        bicone.setOnAction(e->{
            System.out.println(iterateurIcone);
            setIterateurIcone((iterateurIcone + 1) % 4);
            bicone.setStyle("-fx-background-color: " + couleur[(iterateurIcone+1)%4] +";");
            //menuData.getSoloColor().ordinal();
            menuData.setSoloColor(PlayerColor.values()[(iterateurIcone+1)%4]);
            bicone.setGraphic(new ImageView(images[(iterateurIcone+1)%4]));
        });




        //css

        vBoxScene.getStyleClass().add("svBoxScene");
        bsolo.getStyleClass().add("buttonniveaux4");
        bmulti.getStyleClass().add("buttonniveaux4");
        bcharger.getStyleClass().add("buttonniveaux4");
        breprendre.getStyleClass().add("buttonniveaux5");
        bq.getStyleClass().add("buttonniveaux6");

        icone.getStyleClass().add("bin");
        niveaux.getStyleClass().add("bin");
        simple.getStyleClass().add("buttonniveaux1");
        moyen.getStyleClass().add("buttonniveaux2");
        difficile.getStyleClass().add("buttonniveaux3");
        md.getStyleClass().add("buttonjoueur");
        mt.getStyleClass().add("buttonjoueur");
        mq1.getStyleClass().add("buttonjoueur");
        mq2.getStyleClass().add("buttonjoueur");

        sp.getStyleClass().add("s");

        //v.getStyleClass().add("s2");
        //vBoxoptionsCharger.getStyleClass().add("s2");

        for(int i = 0;i < optionsHistory.size(); i++){
            optionsHistory.get(i).getStyleClass().add("buttonjoueur");
        }

/*
        hBoxMillieu.getStyleClass().add("b2");
        chargerList.getStyleClass().add("b1");
*/



        updateMode();
        updateCapture();
        updateLevel();
        updatemultimode();
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


        //vBoxCapture.getStyleClass().add("b2");
        //v.getStyleClass().add("b2");
        //vBoxScene.getStyleClass().add("b1");
        //hBoxBas.getStyleClass().add("b3");
        //vBoxHaut.getStyleClass().add("b1");
        breprendre.setOnAction(this::start);
        bq.setOnAction(e -> Platform.exit());

        root.getChildren().add(vBoxScene);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void updateMode() {
        Toggle selected = tabChoice.getSelectedToggle();
        //System.out.println(tabChoice.getSelectedToggle());
        for (int i = 0; i < optionsButton.length; i++) {
            if (selected == optionsButton[i]) {
                menuData.setMode(MenuMode.values()[i]);
                options.getChildren().setAll(optionsContent[i]);
            }
        }
    }

    private void updateCapture() {
        Toggle selected = historyChoice.getSelectedToggle();
        //System.out.println(historyChoice.getSelectedToggle());
        for (int i = 0; i < optionsHistory.size(); i++) {
            if (selected == optionsHistory.get(i)) {
                //vBoxCapture.
                vBoxCapture.getChildren().setAll(capture.get(i));
            }
        }
    }

    private void updateLevel() {
        Toggle selected = levelChoice.getSelectedToggle();
        for(int i = 0; i < levelButtons.length; i++){
            if(selected == levelButtons[i]){
                menuData.setSoloDifficulty(IADifficulty.values()[i]);
            }
        }
    }


    private void updatemultimode() {
        Toggle selected = mode.getSelectedToggle();
        for(int i = 0; i < optionsMode.length; i++){
            if(selected == optionsMode[i]){
                menuData.setMultiMode(MultiMode.values()[i]);
            }
        }
    }

    private void setIterateurIcone( int val ){
        iterateurIcone = val;
    }

    private void start(ActionEvent event) {
        menuData.save();
        GameApp gameApp = new GameApp(menuData);
        try {
            gameApp.start(stage);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

