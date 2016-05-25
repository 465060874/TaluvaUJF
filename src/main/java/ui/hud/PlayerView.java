package ui.hud;

import data.BuildingType;
import data.ChoosenColors;
import engine.Engine;
import engine.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import map.FieldBuilding;

public class PlayerView extends AnchorPane {

    private static final int WIDTH_TURN = 172;
    private static final int HEIGHT_TURN = 172;
    private static final int WIDTH_NOT_TURN = WIDTH_TURN / 2;
    private static final int HEIGHT_NOT_TURN = HEIGHT_TURN / 2;
    private static final Color BORDER_COLOR = Color.web("303030");

    private final Engine engine;
    private final int index;
    private final ImageView faceView;
    private final BorderPane facePane;
    private final Text[] buildingTexts;
    private final HBox buildingsPane;

    public PlayerView(Engine engine, int index) {
        this.engine = engine;
        this.index = index;

        this.faceView = new ImageView(url());
        corner().anchor(this);
        this.facePane = new BorderPane();
        facePane.setBackground(createBackground(corner().faceRadii()));
        facePane.setBorder(createBorder(corner().faceRadii()));
        corner().anchor(facePane);

        this.buildingsPane = new HBox(20);
        buildingsPane.setAlignment(Pos.CENTER);
        this.buildingTexts = new Text[BuildingType.values().length];
        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            buildingTexts[type.ordinal()] = new Text(String.valueOf(player().getBuildingCount(type)));
            buildingTexts[type.ordinal()].setFont(new Font(14));
            BuildingCanvas buildingCanvas = new BuildingCanvas(FieldBuilding.of(type, player().getColor()));
            buildingsPane.getChildren().addAll(
                    buildingCanvas,
                    buildingTexts[type.ordinal()]);
        }
        buildingsPane.setBackground(createBackground(corner().buildingsRadii()));
        buildingsPane.setBorder(createBorder(corner().buildingsRadii()));

        updateTurn();

        facePane.getChildren().add(faceView);
        getChildren().addAll(buildingsPane, facePane);
    }

    private Background createBackground(CornerRadii cornerRadii) {
        return new Background(new BackgroundFill(color(), cornerRadii, Insets.EMPTY));
    }

    private Border createBorder(CornerRadii cornerRadii) {
        return new Border(new BorderStroke(
                BORDER_COLOR,
                BorderStrokeStyle.SOLID,
                cornerRadii,
                new BorderWidths(3.),
                Insets.EMPTY));
    }

    private PlayerViewCorner corner() {
        return PlayerViewCorner.values()[index];
    }

    void updateTurn() {
        boolean turn = engine.getCurrentPlayer() == player();
        int width = turn ? WIDTH_TURN : WIDTH_NOT_TURN;
        int height = turn ? HEIGHT_TURN : HEIGHT_NOT_TURN;
        faceView.setFitWidth(width);
        faceView.setFitHeight(height);
        facePane.setPrefSize(width, height);
        corner().anchor(buildingsPane, width);

        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            buildingTexts[type.ordinal()].setText(String.valueOf(player().getBuildingCount(type)));
        }
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
