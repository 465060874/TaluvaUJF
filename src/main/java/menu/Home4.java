package menu;

import IA.IA;
import data.PlayerColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import menu.data.MenuData;
import menu.data.MenuMode;
import menu.data.MultiMode;
import menu.data.SavedGame;
import theme.PlayerTheme;
import ui.GameApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaon on 25/05/16.
 */
public class Home4 extends Application {

    static double size_ratio = 580.0 / 800.0;
    static int hauteurScene = 600;
    static int largeurScene = (int) (hauteurScene * size_ratio);

    public static void main(String[] args) {
        launch(args);
    }

    private final MenuData menuData;

    private Stage stage;

    private Button bplay;

    private ToggleGroup modeToggle;
    private ToggleButton[] modeButtons;
    private StackPane modeOptionsPane;
    private Node[] modeOptions;

    private Button bicone;
    private ToggleGroup levelToggle;
    private ToggleButton[] levelButtons;

    private ToggleGroup multiModeToggle;
    private ToggleButton[] multiModeButtons;

    private ToggleGroup reprendreToggle;
    private List<ToggleButton> reprendreButtons;
    private List<Node> reprendreSnapshots;
    private VBox reprendreSnapshotBox = new VBox();

    public Home4() {
        this.menuData = MenuData.load();
    }

    @Override public void start(Stage stage) {
        this.stage = stage;
        //System.out.println(PlayerTheme.BROWN);
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

        vBoxScene.getChildren().addAll(vBoxHaut, hBoxMillieu, hBoxBas);

        //vBoxHaut
        createVBoxHaut(vBoxHaut);

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

        //Font font = Font.loadFont(getClass().getResourceAsStream("f1.ttf").toString(), 20);
        ToggleButton bsolo = new ToggleButton("SOLO");
        //bsolo.setFont(font);
        ToggleButton bmulti = new ToggleButton("MULTI");
        ToggleButton bcharger = new ToggleButton("REPRENDRE");

        bsolo.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bmulti.setPrefSize(largeurScene/3,hauteurScene*2/9);
        bcharger.setPrefSize(largeurScene/3,hauteurScene*2/9);

        this.bplay = new Button();
        bplay.setPrefSize(lv-100,lv-100);
        ImageView playImage = new ImageView("menu/play.png");
        bplay.setGraphic(playImage);
        Button bquit = new Button();
        bquit.setPrefSize(lv-100,lv-100);
        ImageView quitImage = new ImageView("menu/quit.png");
        bquit.setGraphic(quitImage);



        Polygon aPoly = getshapHexagone();
        bsolo.setShape(aPoly);
        bmulti.setShape(aPoly);
        bcharger.setShape(aPoly);
        bplay.setShape(aPoly);
        bquit.setShape(aPoly);

        vBoxsolo.getChildren().add(bsolo);
        vBoxmulti.getChildren().add(bmulti);
        vBoxcharger.getChildren().add(bcharger);
        vBoxreprendre.getChildren().add(bplay);
        vBoxq.getChildren().add(bquit);

        this.modeButtons = new ToggleButton[] { bsolo, bmulti, bcharger };

        this.modeToggle = new ToggleGroup();
        bsolo.setToggleGroup(modeToggle);
        bmulti.setToggleGroup(modeToggle);
        bcharger.setToggleGroup(modeToggle);
        modeToggle.selectedToggleProperty().addListener(e -> updateMode());

        this.modeOptionsPane = new StackPane();
        hBoxMillieu.getChildren().add(modeOptionsPane);

        VBox soloOptions = new VBox(5);
        soloOptions.setAlignment(Pos.CENTER);
        VBox multiOptions = new VBox(20);
        multiOptions.setAlignment(Pos.CENTER);
        multiOptions.setPrefWidth(largeurScene);
        HBox chargerList = new HBox(30);
        chargerList.setAlignment(Pos.CENTER);
        chargerList.setPrefWidth(largeurScene*2/3);
        chargerList.setPadding(new Insets(10,10,10,10));
        this.modeOptions = new Node[] { soloOptions, multiOptions, chargerList };

        //pane de button solo
        //deux buttons : icone /difficulte

        double largeurBorder = largeurScene/10;
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
        vBoxg1.setPrefWidth((largeurScene-25-largeurBorder)/2);
        vBoxm1.setPrefWidth(25);
        vBoxd1.setPrefWidth((largeurScene-25-largeurBorder)/2);

        VBox vBoxg2 = new VBox();
        VBox vBoxm2 = new VBox();
        VBox vBoxd2 = new VBox();
        vBoxg2.setAlignment(Pos.CENTER);
        vBoxd2.setAlignment(Pos.CENTER);
        vBoxm2.setAlignment(Pos.CENTER);
        vBoxg2.setPrefWidth((largeurScene-25-largeurBorder)/2);
        vBoxm2.setPrefWidth(25);
        vBoxd2.setPrefWidth((largeurScene-25-largeurBorder)/2);

        hBoxI.getChildren().addAll(vBoxg1,vBoxm1,vBoxd1);
        hBoxII.getChildren().addAll(vBoxg2,vBoxm2,vBoxd2);
        soloOptions.getChildren().addAll(hBoxI,hBoxII);

        Label icone = new Label("CHOIX COULEUR");
        Label niveaux = new Label("DIFFICULTE");
        icone.setPrefWidth(largeurScene/3);
        icone.setPrefHeight(27);
        niveaux.setPrefWidth(largeurScene/3);
        niveaux.setPrefHeight(27);
        icone.setAlignment(Pos.CENTER);
        niveaux.setAlignment(Pos.CENTER);
        vBoxg1.getChildren().add(icone);
        vBoxd1.getChildren().add(niveaux);

        this.bicone = new Button();
        bicone.setPadding(new Insets(0,0,0,0));
        updateSoloColorButton();

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
        this.levelToggle = new PersistentToggleGroup();
        simple.setToggleGroup(levelToggle);
        moyen.setToggleGroup(levelToggle);
        difficile.setToggleGroup(levelToggle);
        levelToggle.selectToggle(levelButtons[menuData.getSoloDifficulty().ordinal()]);
        levelToggle.selectedToggleProperty().addListener(e -> updateLevel());

        simple.setPrefWidth(largeurScene/3);
        moyen.setPrefWidth(largeurScene/3);
        difficile.setPrefWidth(largeurScene/3);
        vBoxNiveaux.getChildren().addAll(simple,moyen,difficile);

        vBoxg2.getChildren().add(bicone);
        vBoxm2.getChildren().add(iv1);
        vBoxd2.getChildren().add(vBoxNiveaux);


        //pane button multijoueurs

        /*
        ToggleButton md = new ToggleButton("2 JOUEURS");
        ToggleButton mt = new ToggleButton("3 JOUEURS");
        ToggleButton mq1 = new ToggleButton("4 JOUEURS");
        ToggleButton mq2 = new ToggleButton("2 JOUEURS   VS   2 JOUEURS");




        this.multiModeButtons = new ToggleButton[] { md,mt,mq1,mq2 };
        this.multiModeToggle = new ToggleGroup();
        md.setToggleGroup(multiModeToggle);
        mt.setToggleGroup(multiModeToggle);
        mq1.setToggleGroup(multiModeToggle);
        mq2.setToggleGroup(multiModeToggle);
        multiModeToggle.selectToggle(multiModeButtons[menuData.getMultiMode().ordinal()]);
        multiModeToggle.selectedToggleProperty().addListener(e -> updatemultimode());


        md.setPrefWidth(largeurScene/2);
        mt.setPrefWidth(largeurScene/2);
        mq1.setPrefWidth(largeurScene/2);
        mq2.setPrefWidth(largeurScene/2);
*/


        HBox hhaut = new HBox(20);
        HBox hbas  = new HBox(20);
        VBox vg1   = new VBox(10);
        VBox vd1   = new VBox(10);
        VBox vg2   = new VBox(10);
        VBox vd2   = new VBox(10);

        hhaut.setAlignment(Pos.CENTER);
        hbas.setAlignment(Pos.CENTER);
        vg1.setAlignment(Pos.CENTER_RIGHT);
        vd1.setAlignment(Pos.CENTER_LEFT);
        vg2.setAlignment(Pos.CENTER_RIGHT);
        vd2.setAlignment(Pos.CENTER_LEFT);

        hhaut.setPrefWidth(largeurScene);
        hbas.setPrefWidth(largeurScene);
        vg1.setPrefWidth(largeurScene/2);
        vg2.setPrefWidth(largeurScene/2);
        vd1.setPrefWidth(largeurScene/2);
        vd2.setPrefWidth(largeurScene/2);

        //Image masque1 = new Image(getClass().getResourceAsStream(PlayerTheme.WHITE.getImage()));



        //Button button1 = new Button("", new ImageView(PlayerTheme.WHITE.getImage()));
        //Image masque = new Image(PlayerTheme.WHITE.getImage());
        ImageView masque11 = createPlayerImageView(PlayerTheme.WHITE.getImage());
        ImageView masque12 = createPlayerImageView(PlayerTheme.WHITE.getImage());
        ImageView masque13 = createPlayerImageView(PlayerTheme.WHITE.getImage());
        ImageView masque14 = createPlayerImageView(PlayerTheme.WHITE.getImage());

        ImageView masque21 = createPlayerImageView(PlayerTheme.RED.getImage());
        ImageView masque22 = createPlayerImageView(PlayerTheme.RED.getImage());
        ImageView masque23 = createPlayerImageView(PlayerTheme.RED.getImage());
        ImageView masque24 = createPlayerImageView(PlayerTheme.RED.getImage());

        //ImageView masque31 = createPlayerImageView(PlayerTheme.YELLOW.getImage());
        ImageView masque32 = createPlayerImageView(PlayerTheme.YELLOW.getImage());
        ImageView masque33 = createPlayerImageView(PlayerTheme.YELLOW.getImage());
        ImageView masque34 = createPlayerImageView(PlayerTheme.YELLOW.getImage());

        //ImageView masque41 = createPlayerImageView(PlayerTheme.BROWN.getImage());
        //ImageView masque42 = createPlayerImageView(PlayerTheme.BROWN.getImage());
        ImageView masque43 = createPlayerImageView(PlayerTheme.BROWN.getImage());
        ImageView masque44 = createPlayerImageView(PlayerTheme.BROWN.getImage());


        Image vvss = new Image(getClass().getResourceAsStream("vs.png"));
        ImageView  vvssi = new ImageView(vvss);
        vvssi.setFitWidth(15);
        vvssi.setFitWidth(15);

       // Label labelVS  = new Label("VS");
        GridPane b1 = new GridPane();
        b1.setAlignment(Pos.CENTER);
        b1.setVgap(3);
        b1.add(masque11,1,0);
        b1.add(new Label("VS"),2,0);
        b1.add(masque21,3,0);
        ToggleButton md = new ToggleButton("",b1);
        GridPane b2 = new GridPane();
        b2.setAlignment(Pos.CENTER);
        b2.setVgap(5);
        b2.add(masque12,1,0);
        b2.add(new Label("VS"),2,0);
        b2.add(masque22,3,0);
        b2.add(new Label("VS"),4,0);
        b2.add(masque32,5,0);
        ToggleButton mt = new ToggleButton("",b2);
        GridPane b3 = new GridPane();
        b3.setAlignment(Pos.CENTER);
        b3.setVgap(7);
        b3.add(masque13,1,0);
        b3.add(new Label("vs"),2,0);
        b3.add(masque23,3,0);
        b3.add(new Label("vs"),4,0);
        b3.add(masque33,5,0);
        b3.add(new Label("vs"),6,0);
        b3.add(masque43,7,0);
        ToggleButton mq1 = new ToggleButton("",b3);
        GridPane b4 = new GridPane();
        b4.setAlignment(Pos.CENTER);
        b4.setVgap(5);
        b4.add(masque14,1,0);
        b4.add(masque24,2,0);
        //b4.add(vvssi,3,0);
        b4.add(new Label("vs"),3,0);
        b4.add(masque34,4,0);
        b4.add(masque44,5,0);
        ToggleButton mq2 = new ToggleButton("",b4);
        this.multiModeButtons = new ToggleButton[] { md,mt,mq1,mq2 };
        this.multiModeToggle = new PersistentToggleGroup();
        md.setToggleGroup(multiModeToggle);
        mt.setToggleGroup(multiModeToggle);
        mq1.setToggleGroup(multiModeToggle);
        mq2.setToggleGroup(multiModeToggle);
        multiModeToggle.selectToggle(multiModeButtons[menuData.getMultiMode().ordinal()]);
        multiModeToggle.selectedToggleProperty().addListener(e -> updatemultimode());

        //md.setPrefWidth(largeurScene*1/2);
        //mt.setPrefWidth(largeurScene*1/2);
        //mq1.setPrefWidth(largeurScene*1/2);
        //mq2.setPrefWidth(largeurScene*1/2);

        md.setPrefHeight(50);
        mt.setPrefHeight(50);
        mq1.setPrefHeight(50);
        mq2.setPrefHeight(50);
        md.setAlignment(Pos.CENTER);
        mt.setAlignment(Pos.CENTER);

        vg1.getChildren().add(md);
        vd1.getChildren().add(mt);
        vg2.getChildren().add(mq1);
        vd2.getChildren().add(mq2);

        hhaut.getChildren().addAll(vg1,vd1);
        hbas.getChildren().addAll(vg2,vd2);


        //multiOptions.getChildren().addAll(md,mt,mq1,mq2);
        multiOptions.getChildren().addAll(hhaut,hbas);


        this.reprendreButtons = new ArrayList<>();
        this.reprendreToggle = new PersistentToggleGroup();
        this.reprendreSnapshots = new ArrayList<>();
        if (menuData.getSavedGames().isEmpty()) {
            bcharger.setDisable(true);
        }
        else {
            //pane charger
            int kkk = 170;
            for (SavedGame savedGame : menuData.getSavedGames()) {
                reprendreSnapshots.add(new ImageView(savedGame.getImage()));
            }

            VBox vBoxoptionsCharger = new VBox(8);

            chargerList.setAlignment(Pos.CENTER);
            reprendreSnapshotBox.setAlignment(Pos.CENTER);
            vBoxoptionsCharger.setAlignment(Pos.CENTER);
            chargerList.setPrefWidth(largeurScene);
            vBoxoptionsCharger.setPrefWidth(150);
            vBoxoptionsCharger.setPrefHeight(kkk);


            //reprendreSnapshotBox.setPrefHeight(100);
            //vBoxoptionsCharger.setPrefHeight(100);

            for (SavedGame savedGame : menuData.getSavedGames()) {
                ToggleButton savedButton = new ToggleButton(savedGame.getDate());
                savedButton.setPrefWidth(120);
                savedButton.setToggleGroup(reprendreToggle);
                reprendreButtons.add(savedButton);
            }
            if (!reprendreButtons.isEmpty()) {
                reprendreToggle.selectToggle(reprendreButtons.get(0));
            }
            reprendreToggle.selectedToggleProperty().addListener(e -> updateReprendreSnapshot());

            vBoxoptionsCharger.getChildren().addAll(reprendreButtons);

            ScrollPane sp = new ScrollPane();
            VBox v = new VBox();
            v.getChildren().add(sp);
            v.setPrefSize(150, 100);

            sp.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
            vBoxoptionsCharger.setAlignment(Pos.CENTER_LEFT);
            vBoxoptionsCharger.setPadding(new Insets(2, 1, 0, 1));
            sp.setContent(vBoxoptionsCharger);
            if (!reprendreSnapshots.isEmpty()) {
                reprendreSnapshotBox.getChildren().add(reprendreSnapshots.get(0));

            }


            chargerList.getChildren().addAll(reprendreSnapshotBox, v);
            chargerList.setPrefHeight(80);

            v.setAlignment(Pos.CENTER);
            reprendreSnapshotBox.setAlignment(Pos.CENTER);
            //reprendreSnapshotBox.getStyleClass().add("b2");
            //v.getStyleClass().add("b2");

            sp.getStyleClass().add("s");
        }

        bicone.setOnAction(this::updateSoloColor);

        //css

        vBoxScene.getStyleClass().add("svBoxScene");
        bsolo.getStyleClass().add("buttonniveaux4");
        bmulti.getStyleClass().add("buttonniveaux4");
        bcharger.getStyleClass().add("buttonniveaux4");
        bplay.getStyleClass().add("buttonniveaux5");
        bquit.getStyleClass().add("buttonniveaux6");

        icone.getStyleClass().add("bin");
        niveaux.getStyleClass().add("bin");
        simple.getStyleClass().add("buttonniveaux1");
        moyen.getStyleClass().add("buttonniveaux2");
        difficile.getStyleClass().add("buttonniveaux3");
        md.getStyleClass().add("buttonjoueur");
        mt.getStyleClass().add("buttonjoueur");
        mq1.getStyleClass().add("buttonjoueur");
        mq2.getStyleClass().add("buttonjoueur");

        //v.getStyleClass().add("s2");
        //vBoxoptionsCharger.getStyleClass().add("s2");

        for(int i = 0; i < reprendreButtons.size(); i++){
            reprendreButtons.get(i).getStyleClass().add("buttonniveaux7");
        }

/*
        hBoxMillieu.getStyleClass().add("b2");
        chargerList.getStyleClass().add("b1");
*/



        updateMode();
        updateReprendreSnapshot();
        updateLevel();
        updatemultimode();
        updateButtonPlay();
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


        //reprendreSnapshotBox.getStyleClass().add("b2");
        //v.getStyleClass().add("b2");
        //vBoxScene.getStyleClass().add("b1");
        //hBoxBas.getStyleClass().add("b3");
        //vBoxHaut.getStyleClass().add("b1");
        bplay.setOnAction(this::start);
        bquit.setOnAction(e -> Platform.exit());

        root.getChildren().add(vBoxScene);

        if (stage.isShowing()) {
            stage.setX(stage.getX() - (scene.getWidth() - stage.getWidth()) / 2);
            stage.setY(stage.getY() - (scene.getHeight() - stage.getHeight()) / 2);
        }
        stage.setWidth(scene.getWidth());
        stage.setHeight(scene.getHeight() + 36);
        stage.setScene(scene);
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    private Polygon getshapHexagone() {
        double[] path = new double[100];
        for (int q = 0; q < 24; q++) {
            double x = Math.cos(Math.PI / 3.0 * q + Math.PI / 2.0);
            double y = Math.sin(Math.PI / 3.0 * q + Math.PI / 2.0);
            path[q * 2] = x;
            path[q * 2 + 1] = y;
        }

        return new Polygon(path);
    }

    private void createVBoxHaut(VBox vBoxHaut) {
        Image vs1 = new Image(getClass().getResourceAsStream("lol.png"));
        ImageView iv2 = new ImageView();
        iv2.setImage(vs1);

        vBoxHaut.getChildren().add(iv2);
    }

    private ImageView createPlayerImageView(Image image) {
        ImageView masque1 = new ImageView(image);
        masque1.setFitHeight(40);
        masque1.setFitWidth(40);
        return masque1;
    }

    private void updateButtonPlay() {
        bplay.setVisible(menuData.getMode() != null);
    }

    private void updateSoloColor(ActionEvent event) {
        int index = (menuData.getSoloColor().ordinal() + 1) % PlayerColor.values().length;
        menuData.setSoloColor(PlayerColor.values()[index]);
        updateSoloColorButton();
    }

    private void updateSoloColorButton() {
        PlayerTheme playerTheme = PlayerTheme.values()[menuData.getSoloColor().ordinal()];
        bicone.setStyle("-fx-background-color: " + playerTheme.cssDefinition() +";");
        ImageView imageView = new ImageView(playerTheme.getImage());
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        bicone.setGraphic(imageView);
    }

    private void updateMode() {
        Toggle selected = modeToggle.getSelectedToggle();

        if (selected == null) {
            menuData.setMode(null);
            modeOptionsPane.getChildren().clear();
        }
        else {
            for (int i = 0; i < modeButtons.length; i++) {
                if (selected == modeButtons[i]) {
                    menuData.setMode(MenuMode.values()[i]);
                    modeOptionsPane.getChildren().setAll(modeOptions[i]);
                }
            }
        }
        updateButtonPlay();
    }

    private void updateReprendreSnapshot() {
        Toggle selected = reprendreToggle.getSelectedToggle();
        for (int i = 0; i < reprendreButtons.size(); i++) {
            if (selected == reprendreButtons.get(i)) {
                //reprendreSnapshotBox.
                reprendreSnapshotBox.getChildren().setAll(reprendreSnapshots.get(i));
            }
        }
    }

    private void updateLevel() {
        Toggle selected = levelToggle.getSelectedToggle();
        for(int i = 0; i < levelButtons.length; i++){
            if(selected == levelButtons[i]){
                menuData.setSoloDifficulty(IA.values()[i]);
            }
        }
    }


    private void updatemultimode() {
        Toggle selected = multiModeToggle.getSelectedToggle();
        for(int i = 0; i < multiModeButtons.length; i++){
            if(selected == multiModeButtons[i]){
                menuData.setMultiMode(MultiMode.values()[i]);
            }
        }
    }

    private void start(ActionEvent event) {
        if (menuData.getMode() == null) {
            return;
        }

        //menuData.save();
        GameApp gameApp = new GameApp(menuData);
        try {
            gameApp.start(stage);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class PersistentToggleGroup extends ToggleGroup {
    PersistentToggleGroup() {
        super();
        selectedToggleProperty().addListener(this::selectedTogglePropertyChange);
    }

    private void selectedTogglePropertyChange(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (newValue == null) {
            selectToggle(oldValue);
        }
    }
}