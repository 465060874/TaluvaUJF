package ui;

import data.PlayerColor;
import engine.Engine;
import engine.Player;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class Hud extends AnchorPane {

    private final Engine engine;

    Button[] playersIcon;

    StackTileIcon stackTileIcon;

    VBox vboxLeft;
    TextArea textBottom;

    public Hud(Engine engine) {
        this.engine = engine;
        int size = engine.getPlayers().size();
        this.playersIcon = new Button[size];

        List<Player> players = engine.getPlayers();
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

            playersIcon[i].setStyle(
                    "-fx-background-radius: 3em; " +
                            "-fx-min-width: 150px; " +
                            "-fx-min-height: 150px; " +
                            "-fx-max-width: 150px; " +
                            "-fx-max-height: 150px;"
            );

        }

        //player.getBuildingCount(BuildingType.HUT);
        //player.isEliminated();

        this.vboxLeft = new VBox();
        Button left1 = new Button();
        left1.setGraphic(new ImageView("hud/houseHalf.png"));
        left1.setStyle( "-fx-background-color: transparent;");
        left1.setOnAction((e) -> System.out.println("Debug"));
        vboxLeft.getChildren().add(left1);

        Button left2 = new Button();
        left2.setGraphic(new ImageView("hud/save.png"));
        left2.setStyle( "-fx-background-color: transparent;");
        vboxLeft.getChildren().add(left2);

        this.textBottom = new TextArea();
        textBottom.setScrollTop(2);
        textBottom.setMaxWidth(300);
        textBottom.disabledProperty();
        textBottom.setPrefRowCount(2);
        textBottom.setEditable(false);

        if (size > 1) {
            AnchorPane.setLeftAnchor(playersIcon[0], 0.0);
            AnchorPane.setTopAnchor(playersIcon[0], 0.0);

            AnchorPane.setRightAnchor(playersIcon[1], 0.0);
            AnchorPane.setTopAnchor(playersIcon[1], 0.0);
        }

        if (size == 3) {
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
