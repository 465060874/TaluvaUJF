package ui.island;

import data.BuildingType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import map.Building;
import map.Hex;
import map.Island;
import theme.BuildingStyle;
import ui.shape.BuildingShape;
import ui.shape.HexShape;

import java.util.List;

public class WheelOfChoice extends Canvas {

    private final Grid grid;
    private final Island island;
    private final Placement placement;

    private final HexShape hexShape;
    private final BuildingShape buildingShape;

    WheelOfChoice(Island island, Grid grid, Placement placement) {
        super(0, 0);
        this.island = island;
        this.grid = grid;
        this.placement = placement;

        this.hexShape = new HexShape();
        this.buildingShape = new BuildingShape();

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);
    }

    private void resize(Observable event) {

    }

    public void redraw(List<BuildingType> otherAvailableBuildings, Hex hex) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        Stop[] stops = new Stop[]{new Stop(0, Color.rgb(255, 255, 255, 0.0)), new Stop(1, Color.rgb(255, 255, 255, 0.5))};
        final LinearGradient linearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setLineWidth(grid.getHexHeight() * 8);
        gc.setStroke(linearGradient);
        gc.strokeOval(grid.hexToX(hex, getWidth()-grid.getHexHeight()*4),
                grid.hexToY(hex, getHeight())-grid.getHexHeight()*4,
                180 * grid.getScale(),
                180 * grid.getScale());

        setTranslateX(0);
        setTranslateY(0);
    }

    public void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }
}
