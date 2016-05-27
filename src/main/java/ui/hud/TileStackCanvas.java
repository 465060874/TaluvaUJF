package ui.hud;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import engine.Engine;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Building;
import map.Neighbor;
import map.Orientation;
import ui.island.Grid;
import ui.shape.HexShape;
import ui.shape.HexShapeInfo;
import ui.theme.PlacementState;
import ui.theme.Theme;

public class TileStackCanvas extends Canvas {

    private final Engine engine;
    private final Grid grid;
    private final HexShapeInfo volcanoInfo;
    private final HexShapeInfo leftInfo;
    private final HexShapeInfo rightInfo;
    private final HexShape hexShape;

    public TileStackCanvas(Engine engine) {
        super(0, 0);
        this.engine = engine;
        this.grid = new Grid();
        this.volcanoInfo = new HexShapeInfo();
        this.leftInfo = new HexShapeInfo();
        this.rightInfo = new HexShapeInfo();
        this.hexShape = new HexShape();

        grid.scale(0.4);
        volcanoInfo.orientation = Orientation.NORTH;
        volcanoInfo.fieldType = FieldType.VOLCANO;
        volcanoInfo.building = Building.of(BuildingType.NONE, null);
        volcanoInfo.placementState = PlacementState.FLOATING;
        leftInfo.orientation = Orientation.SOUTH_WEST;
        leftInfo.building = Building.of(BuildingType.NONE, null);
        leftInfo.placementState = PlacementState.FLOATING;
        rightInfo.orientation = Orientation.SOUTH_EAST;
        rightInfo.building = Building.of(BuildingType.NONE, null);
        rightInfo.placementState = PlacementState.FLOATING;


        redraw();
    }

    void redraw() {
        double width = 4 * grid.getHexHalfWidth();
        double height = 6 * grid.getHexRadiusY();
        setWidth(width + 10);
        setHeight(height);

        volcanoInfo.x = width/2 + 3;
        volcanoInfo.y = height/2 - grid.getHexRadiusY();
        Neighbor leftNeighbor = Neighbor.SOUTH_WEST;
        leftInfo.x = volcanoInfo.x + grid.neighborToXOffset(leftNeighbor);
        leftInfo.y = volcanoInfo.y + grid.neighborToYOffset(leftNeighbor);
        Neighbor rightNeighbor = Neighbor.SOUTH_EAST;
        rightInfo.x = volcanoInfo.x + grid.neighborToXOffset(rightNeighbor);
        rightInfo.y = volcanoInfo.y + grid.neighborToYOffset(rightNeighbor);

        VolcanoTile tile = engine.getVolcanoTileStack().current();
        if (tile == null) {
            return;
        }

        leftInfo.fieldType = tile.getLeft();
        rightInfo.fieldType = tile.getRight();

        GraphicsContext gc = getGraphicsContext2D();
        hexShape.draw(gc, grid, volcanoInfo);
        hexShape.draw(gc, grid, leftInfo);
        hexShape.draw(gc, grid, rightInfo);
    }
}