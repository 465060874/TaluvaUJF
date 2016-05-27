package ui.hud;

import data.BuildingType;
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

/*
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
        this.grid = new Grid(0, 0, 1);
        this.volcanoInfo = new HexShapeInfo();
        this.leftInfo = new HexShapeInfo();
        this.rightInfo = new HexShapeInfo();
        this.hexShape = new HexShape();

        volcanoInfo.x = 0;
        volcanoInfo.y = -grid.getHexRadiusY();
        volcanoInfo.building = Building.of(BuildingType.NONE, null);
        Neighbor leftNeighbor = Neighbor.SOUTH_WEST;
        leftInfo.x = volcanoInfo.x + grid.neighborToXOffset(leftNeighbor);
        leftInfo.y = volcanoInfo.y + grid.neighborToYOffset(leftNeighbor);
        leftInfo.building = Building.of(BuildingType.NONE, null);
        Neighbor rightNeighbor = Neighbor.SOUTH_EAST;
        rightInfo.x = volcanoInfo.x + grid.neighborToXOffset(rightNeighbor);
        rightInfo.y = volcanoInfo.y + grid.neighborToYOffset(rightNeighbor);
        rightInfo.building = Building.of(BuildingType.NONE, null);

        redraw();
    }

    private void redraw() {
        setWidth(4 * grid.getHexHalfWidth());
        setHeight(6 * grid.getHexHeight() / 2);

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
*/