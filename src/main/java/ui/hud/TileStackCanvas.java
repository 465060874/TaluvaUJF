package ui.hud;

import data.FieldType;
import data.VolcanoTile;
import engine.Engine;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Neighbor;
import map.Orientation;
import theme.IslandTheme;
import ui.island.Grid;
import ui.shape.HexShape;
import theme.HexStyle;

public class TileStackCanvas extends Canvas {

    private final Engine engine;
    private final Grid grid;
    private final HexShape hexShape;

    public TileStackCanvas(Engine engine) {
        super(0, 0);
        this.engine = engine;
        this.grid = new Grid();
        this.hexShape = new HexShape();

        grid.scale(0.4);

        redraw();

        IslandTheme.addListener(this::redraw);
    }

    void redraw() {
        double width = 4 * grid.getHexHalfWidth();
        double height = 6 * grid.getHexRadiusY();
        setWidth(width + 10);
        setHeight(height);

        GraphicsContext gc = getGraphicsContext2D();
        if (engine.getVolcanoTileStack().isEmpty()) {
            gc.clearRect(0, 0, getWidth(), getHeight());
            return;
        }
        VolcanoTile tile = engine.getVolcanoTileStack().current();
        if (tile == null) {
            gc.clearRect(0, 0, getWidth(), getHeight());
            return;
        }

        double volcanoX = width / 2 + 3;
        double volcanoY = height / 2 - grid.getHexRadiusY();
        Neighbor leftNeighbor = Neighbor.SOUTH_WEST;
        double leftX = volcanoX + grid.neighborToXOffset(leftNeighbor);
        double leftY = volcanoY + grid.neighborToYOffset(leftNeighbor);
        Neighbor rightNeighbor = Neighbor.SOUTH_EAST;
        double rightX = volcanoX + grid.neighborToXOffset(rightNeighbor);
        double rightY = volcanoY + grid.neighborToYOffset(rightNeighbor);

        hexShape.draw(gc, grid, volcanoX, volcanoY, 1, FieldType.VOLCANO, Orientation.NORTH, HexStyle.NORMAL);
        hexShape.draw(gc, grid, leftX, leftY, 1, tile.getLeft(), Orientation.SOUTH_WEST, HexStyle.NORMAL);
        hexShape.draw(gc, grid, rightX, rightY, 1, tile.getRight(), Orientation.SOUTH_EAST, HexStyle.NORMAL);
    }
}