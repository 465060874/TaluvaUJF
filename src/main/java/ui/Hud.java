package ui;

import data.ChoosenColors;
import data.PlayerColor;
import engine.Engine;
import engine.Player;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.List;

public class Hud extends AnchorPane {

    private final Engine engine;

    Button[] playersIcon;

    VBox vboxLeft;
    TextFlow textBottom;

    public Hud(Engine engine) {
        this.engine = engine;
        int size = engine.getPlayers().size();
        this.playersIcon = new Button[size];

        List<Player> players = engine.getPlayers();
        String[] radius = new String[]{"0em 10em 10em 10em","10em 0em 10em 10em","10em 10em 10em 0em","10em 10em 0em 10em"};
        for (int i = 0; i < size; i++) {
            Player player = players.get(i);
            PlayerColor color = player.getColor();
            playersIcon[i] = new Button();
            switch (color) {
                case BROWN:
                    playersIcon[i].setGraphic(new ImageView("hud/brownPlayer.png"));
                    break;
                case YELLOW:
                    playersIcon[i].setGraphic(new ImageView("hud/yellowPlayer.png"));
                    break;
                case RED:
                    playersIcon[i].setGraphic(new ImageView("hud/redPlayer.png"));
                    break;
                case WHITE:
                    playersIcon[i].setGraphic(new ImageView("hud/whitePlayer.png"));
                    break;
            }

            String iconBgColor = "";
            switch(color) {
                case BROWN: iconBgColor = ChoosenColors.BROWN.cssDefinition();
                    break;
                case YELLOW: iconBgColor = ChoosenColors.YELLOW.cssDefinition();
                    break;
                case RED: iconBgColor = ChoosenColors.RED.cssDefinition();
                    break;
                case WHITE: iconBgColor = ChoosenColors.WHITE.cssDefinition();
                    break;
            }

            playersIcon[i].setStyle(
                            "-fx-background-color: " + iconBgColor +" ;" +
                                    "-fx-background-radius: " + radius[i] +" ;" +
                                    "-fx-min-width: 100px; " +
                                    "-fx-min-height: 100px; " +
                                    "-fx-max-width: 172px; " +
                                    "-fx-max-height: 172px;" +
                                    "-fx-border-color: rgb(0, 0, 0);" +
                                    "-fx-border-radius: " + radius[i]+ " ;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-border-style: solid;" +
                            "-fx-min-width: 100px; " +
                            "-fx-min-height: 100px; " +
                            "-fx-max-width: 172px; " +
                            "-fx-max-height: 172px;"
            );

        }

        //player.getBuildingCount(BuildingType.HUT);
        //player.isEliminated();

        this.vboxLeft = new VBox();
        Button left1 = new Button();
        left1.setGraphic(new ImageView("hud/101__home.png"));
        left1.setStyle( "-fx-background-color: rgb(0,0,0, 0);");
        left1.setOnAction((e) -> System.out.println("Debug"));
        vboxLeft.getChildren().add(left1);

        Button left2 = new Button();
        left2.setGraphic(new ImageView("hud/027__download.png"));
        left2.setStyle( "-fx-background-color: rgb(0,0,0, 0);");
        vboxLeft.getChildren().add(left2);
        //vboxLeft.setSpacing();

        Font font = new Font(16);
        Text firstLine = new Text("Text goes here !");
        firstLine.setFont(font);
        Text secondLine = new Text("And second line is there");
        secondLine.setFont(font);
        this.textBottom = new TextFlow(firstLine, new Text("\n"), secondLine);
        textBottom.setTextAlignment(TextAlignment.CENTER);
        textBottom.setPadding(new Insets(0, 0, 10, 0));

        if (size > 1) {
            AnchorPane.setLeftAnchor(playersIcon[0], 0.0);
            AnchorPane.setTopAnchor(playersIcon[0], 0.0);

            AnchorPane.setRightAnchor(playersIcon[1], 0.0);
            AnchorPane.setTopAnchor(playersIcon[1], 0.0);
        }

        if (size > 2) {
            AnchorPane.setLeftAnchor(playersIcon[2], 0.0);
            AnchorPane.setBottomAnchor(playersIcon[2], 0.0);
        }

        if (size > 3) {
            AnchorPane.setRightAnchor(playersIcon[3], 0.0);
            AnchorPane.setBottomAnchor(playersIcon[3], 0.0);
        }

        AnchorPane.setLeftAnchor(vboxLeft, 0.0);
        AnchorPane.setBottomAnchor(textBottom, 0.0);


        for (Button button : playersIcon) {
            getChildren().add(button);
        }

        getChildren().add(vboxLeft);
        getChildren().add(textBottom);

        widthProperty().addListener(this::resizeWidth);
        heightProperty().addListener(this::resizeHeight);
        layoutBoundsProperty().addListener(this::resizeWidth);
        layoutBoundsProperty().addListener(this::resizeHeight);
    }

    private void resizeWidth(Observable observable) {
        AnchorPane.setLeftAnchor(textBottom, (getWidth() - textBottom.getWidth()) / 2);
        layoutChildren();
    }

    private void resizeHeight(Observable observable) {
        AnchorPane.setTopAnchor(vboxLeft, (getHeight() - vboxLeft.getHeight()) / 2);
        layoutChildren();
    }
}
