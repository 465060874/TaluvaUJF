package ui.hud;

import data.ChoosenColors;
import engine.Engine;
import engine.Player;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class PlayerView extends AnchorPane {

    private static final int WIDTH_TURN = 172;
    private static final int HEIGHT_TURN = 172;
    private static final int WIDTH_NOT_TURN = WIDTH_TURN / 2;
    private static final int HEIGHT_NOT_TURN = HEIGHT_TURN / 2;
    public static final Color BORDER_COLOR = Color.web("303030");

    private final Engine engine;
    private final int index;
    private final ImageView imageView;
    private final BorderPane imagePane;

    public PlayerView(Engine engine, int index) {
        this.engine = engine;
        this.index = index;

        PlayerViewCorner corner = PlayerViewCorner.values()[index];

        this.imageView = new ImageView(url());
        corner.anchor(this);
        this.imagePane = new BorderPane();
        imagePane.setBackground(new Background(new BackgroundFill(color(), corner.faceRadii(), Insets.EMPTY)));

        imagePane.setBorder(new Border(new BorderStroke(
                BORDER_COLOR,
                BorderStrokeStyle.SOLID,
                corner.faceRadii(),
                new BorderWidths(3.),
                Insets.EMPTY)));
        corner.anchor(imageView);
        updateTurn();

        imagePane.getChildren().add(imageView);
        getChildren().add(imagePane);
    }

    void updateTurn() {
        boolean turn = engine.getCurrentPlayer() == player();
        int width = turn ? WIDTH_TURN : WIDTH_NOT_TURN;
        int height = turn ? HEIGHT_TURN : HEIGHT_NOT_TURN;
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imagePane.setPrefSize(width, height);
    }

    private Player player() {
        return engine.getPlayers().get(index);
    }

    private Paint color() {
        switch (player().getColor()) {
            case BROWN:  return ChoosenColors.BROWN.color();
            case YELLOW: return ChoosenColors.YELLOW.color();
            case RED:    return ChoosenColors.RED.color();
            case WHITE:  return ChoosenColors.WHITE.color();
        }

        throw new IllegalStateException();
    }

    private String url() {
        switch (player().getColor()) {
            case BROWN:  return "hud/brownPlayer.png";
            case YELLOW: return "hud/yellowPlayer.png";
            case RED:    return "hud/redPlayer.png";
            case WHITE: return "hud/whitePlayer.png";
        }

        throw new IllegalStateException();
    }
}
