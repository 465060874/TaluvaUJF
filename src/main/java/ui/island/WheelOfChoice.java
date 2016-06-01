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

import java.util.ArrayList;
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

        drawWheelOfChoiseBuildings(
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

    private void drawWheelOfChoiseBuildings(GraphicsContext gc, Grid grid,
                                           double xCenter, double yCenter,
                                           double oWidth, List<BuildingType> otherAvailableBuildings,
                                           BuildingType current) {

        ArrayList<WheelOfChoiceBuilding> wheel = new ArrayList<>(3);
        BuildingType previous = current;
        previous = previous.previousBuilding();
        while (previous != current) {
            if (otherAvailableBuildings.contains(previous)) {
                wheel.add(new WheelOfChoiceBuilding(previous, true));
            } else {
                wheel.add(new WheelOfChoiceBuilding(previous, false));
            }
            previous = previous.previousBuilding();
        }

        double angle = -15;
        double x = xCenter + oWidth * Math.cos(Math.toRadians(angle));
        double y = yCenter + oWidth * Math.sin(Math.toRadians(angle));
        for (WheelOfChoiceBuilding wheelE : wheel) {
            /*
            if (wheelE.isValid()) {
                draw(gc, grid, x, y, 1, Building.of(wheelE.getBuildingType(), PlayerColor.WHITE), BuildingStyle.WHEELVALID);
            } else {
                draw(gc, grid, x, y, 1, Building.of(wheelE.getBuildingType(), PlayerColor.RED), BuildingStyle.WHEELINVALID);
            }
            */
            angle += 80;
            x = xCenter + oWidth * Math.cos(angle);
            y = yCenter + oWidth * Math.sin(angle);
        }
    }
}
