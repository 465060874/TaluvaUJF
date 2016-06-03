package ui.hud;

import data.BuildingType;
import engine.Engine;
import engine.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import theme.PlayerTheme;
import ui.island.Placement;

public class PlayerView extends Canvas {

    static final int WIDTH_TURN = 172;
    static final int HEIGHT_TURN = 172;
    static final int WIDTH_NOT_TURN = WIDTH_TURN / 2;
    static final int HEIGHT_NOT_TURN = HEIGHT_TURN / 2;
    private static final Color BORDER_COLOR = Color.web("303030");

    private static final int LIGHT_MIN_Z = 200;
    private static final int LIGHT_MAX_Z = 300;
    private static final int LIGHT_DIFF_Z = 10;

    private final Engine engine;
    private final int index;
    private final Placement placement;

    private final Image faceImage;

    private final Light.Point light;
    private double lightDiff;

    public PlayerView(Engine engine, int index, Placement placement) {
        super(WIDTH_NOT_TURN, HEIGHT_NOT_TURN);
        this.engine = engine;
        this.index = index;
        this.placement = placement;

        this.faceImage = PlayerTheme.of(player().getColor()).getImage();
        corner().anchor(this);

        this.light = new Light.Point(0, 0, 60, Color.WHITE);
        this.lightDiff = LIGHT_DIFF_Z;

        /*this.buildingsCanvas = new HBox(20);
        buildingsCanvas.setAlignment(Pos.CENTER);
        this.buildingTexts = new Text[BuildingType.values().length];
        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            buildingTexts[type.ordinal()] = new Text(String.valueOf(player().getBuildingCount(type)));
            buildingTexts[type.ordinal()].setFont(new Font(14));
            BuildingCanvas buildingCanvas = new BuildingCanvas(Building.of(type, player().getColor()));
            buildingsCanvas.getChildren().addAll(
                    buildingCanvas,
                    buildingTexts[type.ordinal()]);
        }
        buildingsCanvas.setBackground(createBackground(corner().buildingsRadii()));
        buildingsCanvas.setBorder(createBorder(corner().buildingsRadii()));*/

        updateTurn();
    }

    private PlayerViewCorner corner() {
        return PlayerViewCorner.values()[index];
    }

    void updateTurn() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        light.setZ(250);
        lightDiff = LIGHT_DIFF_Z;
        redraw(gc);
    }

    private void redraw(GraphicsContext gc) {
        boolean turn = engine.getCurrentPlayer() == player();
        PlayerViewCorner corner = corner();
        int width = turn ? WIDTH_TURN : WIDTH_NOT_TURN;
        int height = turn ? HEIGHT_TURN : HEIGHT_NOT_TURN;
        setWidth(width);
        setHeight(height);

        gc.setFill(color());
        if (turn && !player().isHuman()) {
            gc.setEffect(new Lighting(light));
        }
        gc.fillOval(
                corner.arcX(getWidth()), corner.arcY(getHeight()),
                getWidth() * 2, getHeight() * 2);
        gc.setEffect(null);

        gc.drawImage(faceImage,
                corner.imageX(width), corner.imageY(height),
                width / 2, height / 2);


        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            //gc.fillOval(corner.hutX(width), corner.hutY(height), 100, 100);
            // drawBuilding at the right position
            // drawCount at the right position
        }
    }

    private Player player() {
        return engine.getPlayers().get(index);
    }

    private Paint color() {
        if (player().isEliminated()) {
            return PlayerTheme.ELIMINATED;
        }
        switch (player().getColor()) {
            case BROWN:  return PlayerTheme.BROWN.color();
            case YELLOW: return PlayerTheme.YELLOW.color();
            case RED:    return PlayerTheme.RED.color();
            case WHITE:  return PlayerTheme.WHITE.color();
        }

        throw new IllegalStateException();
    }

    public void tick() {
        if (player() != engine.getCurrentPlayer() || player().isHuman()) {
            return;
        }

        if (lightDiff > 0 && light.getZ() >= LIGHT_MAX_Z
                || lightDiff < 0 && light.getZ() <= LIGHT_MIN_Z) {
            lightDiff *= -1;
        }

        double newLightZ = (light.getZ() - LIGHT_MIN_Z + lightDiff) + LIGHT_MIN_Z;
        light.setZ(newLightZ);
        redraw(getGraphicsContext2D());
    }
}
