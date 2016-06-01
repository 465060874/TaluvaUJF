package ui.island;

import data.BuildingType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import map.Hex;
import ui.shape.BuildingShape;

import java.util.List;

public class WheelOfChoice extends Canvas {

    public static final Color TRANSPARENT = Color.rgb(255, 255, 255, 0.0);
    public static final int OWIDTH_MULT = 8;
    public static final int HALF_OWIDTH_MULT = OWIDTH_MULT / 2;
    public static final int OWIDTH = 180;
    private final Grid grid;
    private final Placement placement;

    private final BuildingShape buildingShape;
    private final Color selected = Color.web("ea3434");

    WheelOfChoice(Grid grid, Placement placement) {
        super(0, 0);
        this.grid = grid;
        this.placement = placement;

        this.buildingShape = new BuildingShape();
    }

    public void redraw(List<BuildingType> otherAvailableBuildings, Hex hex, boolean valid, BuildingType current) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        Stop[] stops = new Stop[]{new Stop(0, TRANSPARENT), new Stop(1, Color.WHITE)};
        final LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setLineWidth(grid.getHexHeight() * OWIDTH_MULT);
        gc.setStroke(linearGradient);
        double startOvalX = grid.hexToX(hex, getWidth() - grid.getHexHeight() * HALF_OWIDTH_MULT);
        double startOvalY = grid.hexToY(hex, getHeight() - grid.getHexHeight() * HALF_OWIDTH_MULT);
        gc.strokeOval(startOvalX,
                startOvalY,
                OWIDTH * grid.getScale(),
                OWIDTH * grid.getScale());


        double centerX = startOvalX + OWIDTH/2 * grid.getScale();
        double centerY = startOvalY + OWIDTH/2 * grid.getScale();

        buildingShape.drawWheelOfChoiseBuildings(
                gc, grid,
                centerX, centerY,
                OWIDTH/2 * grid.getScale(),
                otherAvailableBuildings, current);

        setTranslateX(0);
        setTranslateY(0);
    }

    public void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }
}
