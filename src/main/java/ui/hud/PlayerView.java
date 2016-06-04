package ui.hud;

import data.BuildingType;
import engine.Engine;
import engine.EngineStatus;
import engine.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import map.Building;
import theme.BuildingStyle;
import theme.PlayerTheme;
import ui.island.Grid;
import ui.island.Placement;
import ui.shape.BuildingShape;

public class PlayerView extends Canvas {

    static final int WIDTH_TURN = 250;
    static final int HEIGHT_TURN = 250;
    static final int WIDTH_NOT_TURN = (int) (2.0 * WIDTH_TURN / 3.0);
    static final int HEIGHT_NOT_TURN = (int) (2.0 * HEIGHT_TURN / 3.0);

    private static final int LIGHT_MIN_Z = 150;
    private static final int LIGHT_MAX_Z = 700;
    private static final int LIGHT_DIFF_Z = 25;

    private final Engine engine;
    private final int index;
    private final Placement placement;
    private final Grid grid;

    private final Image faceImage;
    private final BuildingShape shape;

    private final Light.Point light;
    private double lightDiff;

    public PlayerView(Engine engine, int index, Placement placement) {
        super(WIDTH_NOT_TURN, HEIGHT_NOT_TURN);
        this.engine = engine;
        this.index = index;
        this.placement = placement;
        this.grid = new Grid();

        this.faceImage = PlayerTheme.of(player().getColor()).getImage();
        corner().anchor(this);
        this.shape = new BuildingShape();

        this.light = new Light.Point(0, 0, 60, Color.WHITE);
        this.lightDiff = LIGHT_DIFF_Z;

        updateTurn();
    }

    private PlayerViewCorner corner() {
        return PlayerViewCorner.values()[index];
    }

    void updateTurn() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        light.setZ(LIGHT_MAX_Z);
        lightDiff = LIGHT_DIFF_Z;
        redraw(gc);
    }

    private void redraw(GraphicsContext gc) {
        boolean turn = turn();
        PlayerViewCorner corner = corner();
        int width = turn ? WIDTH_TURN : WIDTH_NOT_TURN;
        int height = turn ? HEIGHT_TURN : HEIGHT_NOT_TURN;
        setWidth(width);
        setHeight(height);

        gc.setEffect(new Lighting(light));
        gc.setFill(Color.BLACK);
        gc.fillOval(
                corner.ovalX(getWidth()), corner.ovalY(getHeight()),
                getWidth() * 2, getHeight() * 2);
        gc.setFill(color());
        gc.fillOval(
                corner.ovalX2(getWidth() / 2), corner.ovalY2(getHeight() / 2),
                getWidth(), getHeight());
        gc.setEffect(null);

        gc.drawImage(faceImage,
                corner.imageX(width), corner.imageY(height),
                width / 2 - 20, height / 2 - 20);

        for (BuildingType type : BuildingType.values()) {
            double x;
            double y;
            double yCountOffset;
            double scale;
            switch (type) {
                case TEMPLE:
                    x = corner.templeX(width);
                    y = corner.templeY(height);
                    yCountOffset = -(2 * height) / HEIGHT_TURN;
                    scale = 0.6;
                    break;
                case TOWER:
                    x = corner.towerX(width);
                    y = corner.towerY(height);
                    yCountOffset = -(8 * height) / HEIGHT_TURN;
                    scale = 0.6;
                    break;
                case HUT:
                    x = corner.hutX(width);
                    y = corner.hutY(height);
                    yCountOffset = (4 * height) / HEIGHT_TURN;
                    scale = 1;
                    break;
                default:
                    continue;
            }

            if (!turn) scale /= 2;
            grid.setAbsoluteScale(scale);
            Building building = Building.of(type, player().getColor());
            shape.draw(gc, grid,
                    x, y, 1,
                    building, BuildingStyle.NORMAL);

            x += corner.buildingCountOffsetX(width) * 1.5;
            y += yCountOffset;

            gc.save();
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(turn ? 12 : 8));
            gc.fillText(Integer.toString(player().getBuildingCount(type)), x, y);
            gc.restore();
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

    private boolean turn() {
        return engine.getStatus() instanceof EngineStatus.Running
                && player() == engine.getCurrentPlayer();
    }

    public void tick() {
        if (!turn() || player().isHuman()) {
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
