package ui.hud;

import data.BuildingType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Building;
import ui.island.Grid;
import ui.shape.BuildingShapes;
import ui.shape.HexShapeInfo;
import ui.theme.PlacementState;

public class BuildingCanvas extends Canvas {

    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;

    public BuildingCanvas(Building building) {
        super(WIDTH, HEIGHT);

        GraphicsContext gc = getGraphicsContext2D();
        Grid grid = new Grid();
        grid.scale(building.getType() == BuildingType.HUT
                ? 0.8
                : 0.4);
        HexShapeInfo info = new HexShapeInfo();
        info.level = 1;
        info.placementState = PlacementState.NONE;
        info.x = WIDTH / 2;
        info.y = HEIGHT / 2;
        info.building = building;
        BuildingShapes.drawBuilding(gc, grid, info);
    }
}
