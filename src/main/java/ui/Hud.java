package ui;

import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class Hud extends AnchorPane {

    int nbPlayer;
    Button player1;
    Button player2;
    Button player3;
    Button player4;
    StackTileIcon stackTileIcon;


    VBox vboxLeft;
    TextArea textBottom;

    public Hud(int nbPlayer) {
        this.nbPlayer = nbPlayer;
        this.player1 = new Button("Player1, Score");
        this.player2 = new Button("Player2, Score");
        this.player3 =  new Button("Player3, Score");
        this.player4 = new Button("Player4, Score");

        this.vboxLeft = new VBox();
        Button left1 = new Button();
        left1.setGraphic(new ImageView("hud/houseHalf.png"));
        left1.setStyle( "-fx-background-color: transparent;");
        left1.setOnAction((e) -> System.out.println("Debug"));
        vboxLeft.getChildren().add(left1);

        Button left2 = new Button();
        left2.setGraphic(new ImageView("hud/settingsHalf.png"));
        left2.setStyle( "-fx-background-color: transparent;");
        vboxLeft.getChildren().add(left2);

        this.textBottom = new TextArea();
        textBottom.setScrollTop(2);
        textBottom.setMaxWidth(300);
        textBottom.disabledProperty();
        textBottom.setPrefRowCount(2);
        textBottom.setEditable(false);

        AnchorPane.setLeftAnchor(player1, 0.0);
        AnchorPane.setTopAnchor(player1, 0.0);

        AnchorPane.setRightAnchor(player2, 0.0);
        AnchorPane.setTopAnchor(player2, 0.0);

        AnchorPane.setLeftAnchor(player3, 0.0);
        AnchorPane.setBottomAnchor(player3, 0.0);

        AnchorPane.setRightAnchor(player4, 0.0);
        AnchorPane.setBottomAnchor(player4, 0.0);

        AnchorPane.setLeftAnchor(vboxLeft, 0.0);
        AnchorPane.setBottomAnchor(textBottom, 0.0);

        getChildren().add(player1);
        getChildren().add(player2);
        getChildren().add(player3);
        getChildren().add(player4);
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
