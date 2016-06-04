package ui.hud;

import data.FieldType;
import data.VolcanoTile;
import engine.Engine;
import engine.EngineStatus;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import map.Neighbor;
import map.Orientation;
import theme.HexStyle;
import theme.IslandTheme;
import ui.island.Grid;
import ui.shape.HexShape;

public class TileStackCanvas extends Canvas {

    public static final int MAX_DISPLAYED_STACK_SIZE = 10;

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
        boolean build = engine.getStatus() == EngineStatus.PENDING_START
                || engine.getStatus().getStep() == EngineStatus.TurnStep.BUILD;
        int stackSize = engine.getVolcanoTileStack().size();
        String stackSizeStr = Integer.toString(stackSize);
        stackSize = Math.min(MAX_DISPLAYED_STACK_SIZE, stackSize);

        double width = 4 * grid.getHexHalfWidth();
        double height = 6 * grid.getHexRadiusY() + grid.getHexHeight() * stackSize;
        setWidth(width + 10);
        setHeight(height);

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        if (engine.getVolcanoTileStack().isEmpty()) {
            return;
        }
        VolcanoTile tile = engine.getVolcanoTileStack().current();
        if (tile == null) {
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

        hexShape.draw(gc, grid, volcanoX, volcanoY, stackSize,
                FieldType.VOLCANO, Orientation.NORTH, HexStyle.NORMAL, build);
        hexShape.draw(gc, grid, leftX, leftY, stackSize,
                tile.getLeft(), Orientation.SOUTH_WEST, HexStyle.NORMAL, build);
        hexShape.draw(gc, grid, rightX, rightY, stackSize,
                tile.getRight(), Orientation.SOUTH_EAST, HexStyle.NORMAL, build);

        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BOTTOM);
        gc.setFont(new Font(16));
        gc.fillText(stackSizeStr, volcanoX, volcanoY - 10);
    }
}
