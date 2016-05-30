package ui.hud;

import data.BuildingType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Building;
import ui.island.Grid;
import ui.shape.BuildingShape;
import theme.BuildingStyle;

public class BuildingCanvas extends Canvas {

    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;

    private final Building building;
    private final Grid grid;

    private final BuildingShape shape;

    public BuildingCanvas(Building building) {
        super(WIDTH, HEIGHT);
        this.building = building;
        this.grid = new Grid();
        grid.scale(building.getType() == BuildingType.HUT ? 0.8 : 0.4);

        this.shape = new BuildingShape();
        redraw();
    }

    private void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        shape.draw(gc, grid,
                WIDTH / 2,
                HEIGHT / 2,
                1, building, BuildingStyle.NORMAL);
    }
}
