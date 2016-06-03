package ui.shape;

import data.FieldType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import map.Orientation;
import theme.HexStyle;
import theme.IslandTheme;
import ui.island.Grid;

public class HexShape {

    private static final int HEXAGON_POINTS = 6;
    private static final int HEXAGON_BORDER_POINTS = 5;
    private static final int HEXAGON_BORDER2_POINTS = 3;
    private static final int BOTTOM_POINTS = 6;
    private static final int BOTTOM_BORDER_POINTS = 5;
    public static final float STROKE_WIDTH = 3f;
    private static final float LASTPLAYED_STROKE_WIDTH = STROKE_WIDTH * 2f;
    private static final double FADEDRATIO = .8f;

    private final double[] hexagonX;
    private final double[] hexagonY;
    private final double[] hexagonBorderX;
    private final double[] hexagonBorderY;
    private final double[] hexagonBorder2X;
    private final double[] hexagonBorder2Y;
    private final double[] bottomX;
    private final double[] bottomY;
    private final double[] bottomBorderX;
    private final double[] bottomBorderY;
    private final double[] fadedBorderX;
    private final double[] fadedBorderY;

    public HexShape() {
        this.hexagonX = new double[HEXAGON_POINTS];
        this.hexagonY = new double[HEXAGON_POINTS];
        this.hexagonBorderX = new double[HEXAGON_BORDER_POINTS];
        this.hexagonBorderY = new double[HEXAGON_BORDER_POINTS];
        this.hexagonBorder2X = new double[HEXAGON_BORDER2_POINTS];
        this.hexagonBorder2Y = new double[HEXAGON_BORDER2_POINTS];
        this.bottomX = new double[BOTTOM_POINTS];
        this.bottomY = new double[BOTTOM_POINTS];
        this.bottomBorderX = new double[BOTTOM_BORDER_POINTS];
        this.bottomBorderY = new double[BOTTOM_BORDER_POINTS];

        // Hexagone de selection
        this.fadedBorderX = new double [HEXAGON_POINTS + 1];
        this.fadedBorderY = new double [HEXAGON_POINTS + 1];
    }

    private void updatePolygon(Grid grid, double x, double y, int level, Orientation orientation) {
        double hexHeight = grid.getHexHeight();
        double y2 = y - (level - 1) * hexHeight;
        double halfWidth = grid.getHexHalfWidth();
        double midY = grid.getHexRadiusY() / 2;
        double bottomDepth = hexHeight * level;

        hexagonX[0] = x - halfWidth;
        hexagonY[0] = y2 + midY;

        hexagonX[1] = x;
        hexagonY[1] = y2 + grid.getHexRadiusY();

        hexagonX[2] = x + halfWidth;
        hexagonY[2] = y2 + midY;

        hexagonX[3] = x + halfWidth;
        hexagonY[3] = y2 - midY;

        hexagonX[4] = x;
        hexagonY[4] = y2 - grid.getHexRadiusY();

        hexagonX[5] = x - halfWidth;
        hexagonY[5] = y2 - midY;

        double y2Faded = y - (level - 1) * hexHeight;
        double halfWidthInner = grid.getHexHalfWidth() * FADEDRATIO;
        double midYInner = (grid.getHexRadiusY() / 2) * FADEDRATIO;

        fadedBorderX[0] = x - halfWidthInner;
        fadedBorderY[0] = y2Faded + midYInner;

        fadedBorderX[1] = x;
        fadedBorderY[1] = y2Faded + grid.getHexRadiusY() * FADEDRATIO;

        fadedBorderX[2] = x + halfWidthInner;
        fadedBorderY[2] = y2Faded + midYInner;

        fadedBorderX[3] = x + halfWidthInner;
        fadedBorderY[3] = y2Faded - midYInner;

        fadedBorderX[4] = x;
        fadedBorderY[4] = y2Faded - grid.getHexRadiusY() * FADEDRATIO;

        fadedBorderX[5] = x - halfWidthInner;
        fadedBorderY[5] = y2Faded - midYInner;

        fadedBorderX[6] = fadedBorderX[0];
        fadedBorderY[6] = fadedBorderY[0];

        int orientationOffset = orientationOffset(orientation);

        hexagonBorderX[0] = hexagonX[(orientationOffset) % 6];
        hexagonBorderY[0] = hexagonY[(orientationOffset) % 6];

        hexagonBorderX[1] = hexagonX[(orientationOffset + 1) % 6];
        hexagonBorderY[1] = hexagonY[(orientationOffset + 1) % 6];

        hexagonBorderX[2] = hexagonX[(orientationOffset + 2) % 6];
        hexagonBorderY[2] = hexagonY[(orientationOffset + 2) % 6];

        hexagonBorderX[3] = hexagonX[(orientationOffset + 3) % 6];
        hexagonBorderY[3] = hexagonY[(orientationOffset + 3) % 6];

        hexagonBorderX[4] = hexagonX[(orientationOffset + 4) % 6];
        hexagonBorderY[4] = hexagonY[(orientationOffset + 4) % 6];

        hexagonBorder2X[0] = hexagonBorderX[4];
        hexagonBorder2Y[0] = hexagonBorderY[4];

        hexagonBorder2X[1] = hexagonX[(orientationOffset + 5) % 6];
        hexagonBorder2Y[1] = hexagonY[(orientationOffset + 5) % 6];

        hexagonBorder2X[2] = hexagonBorderX[0];
        hexagonBorder2Y[2] = hexagonBorderY[0];

        bottomX[0] = hexagonX[0];
        bottomY[0] = hexagonY[0];

        bottomX[1] = hexagonX[0];
        bottomY[1] = hexagonY[0] + bottomDepth;

        bottomX[2] = hexagonX[1];
        bottomY[2] = hexagonY[1] + bottomDepth;

        bottomX[3] = hexagonX[2];
        bottomY[3] = hexagonY[2] + bottomDepth;

        bottomX[4] = hexagonX[2];
        bottomY[4] = hexagonY[2];

        bottomX[5] = hexagonX[1];
        bottomY[5] = hexagonY[1];

        bottomBorderX[0] = hexagonX[0];
        bottomBorderX[1] = hexagonX[0];
        bottomBorderX[2] = hexagonX[1];
        bottomBorderX[3] = hexagonX[2];
        bottomBorderX[4] = hexagonX[2];
    }

    private int orientationOffset(Orientation orientation) {
        switch (orientation) {
            case NORTH:
                return 2;
            case NORTH_WEST:
                return 3;
            case NORTH_EAST:
                return 1;
            case SOUTH:
                return 5;
            case SOUTH_WEST:
                return 4;
            case SOUTH_EAST:
                return 0;
        }

        throw new IllegalStateException();
    }

    private void bottomBorderLevel(Grid grid, int level, int maxLevel) {
        double hexHeight = grid.getHexHeight();
        double bottomDepth2 = hexHeight * (maxLevel - level + 1);
        double bottomDepth1 = bottomDepth2 - hexHeight;
        if (level == maxLevel) {
            bottomDepth1 += grid.getScale();
        }


        bottomBorderY[0] = hexagonY[0] + bottomDepth1;
        bottomBorderY[1] = hexagonY[0] + bottomDepth2;
        bottomBorderY[2] = hexagonY[1] + bottomDepth2;
        bottomBorderY[3] = hexagonY[2] + bottomDepth2;
        bottomBorderY[4] = hexagonY[2] + bottomDepth1;
    }

    public void draw(GraphicsContext gc, Grid grid,
                     double x, double y, int level, FieldType fieldType, Orientation orientation, HexStyle style) {
        draw(gc, grid, x, y, level, fieldType, orientation, style, false);
    }

    public void draw(GraphicsContext gc, Grid grid,
                     double x, double y, int level,
                     FieldType fieldType, Orientation orientation, HexStyle style,
                     boolean hidden) {
        updatePolygon(grid, x, y, Math.max(1, level), orientation);

        // Fill shape
        gc.setEffect(IslandTheme.getCurrent().getTileTopEffect(grid, style));
        gc.setFill(IslandTheme.getCurrent().getTileTopPaint(fieldType, style, orientation));
        if (hidden) {
            gc.setFill(IslandTheme.getCurrent().getTileBottomPaint(style, level));
        }

        gc.fillPolygon(hexagonX, hexagonY, HEXAGON_POINTS);

        gc.setEffect(IslandTheme.getCurrent().getTileBottomEffect(grid, style));
        gc.setFill(IslandTheme.getCurrent().getTileBottomPaint(style, level));
        gc.fillPolygon(bottomX, bottomY, HEXAGON_POINTS);
        gc.setEffect(null);

        // Draw borders
        gc.setStroke(IslandTheme.getCurrent().getTileBorderPaint(style));
        if (style == HexStyle.LASTPLAYED) {
            gc.setLineWidth(LASTPLAYED_STROKE_WIDTH * grid.getScale());
        } else {
            gc.setLineWidth(STROKE_WIDTH * grid.getScale());
        }
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        gc.strokePolyline(hexagonBorderX, hexagonBorderY, HEXAGON_BORDER_POINTS);
        gc.setLineWidth(STROKE_WIDTH * grid.getScale());
        for (int i = 1; i <= level; i++) {
            bottomBorderLevel(grid, i, level);
            gc.strokePolyline(bottomBorderX, bottomBorderY, BOTTOM_BORDER_POINTS);
        }

        // Ajout des cases non jouable en contour rouge
        if (style == HexStyle.FADED || style == HexStyle.FADEDLASTPLAYED) {
            gc.setStroke(IslandTheme.getCurrent().getInnerBorderPaint(style));
            gc.strokePolyline(fadedBorderX, fadedBorderY, HEXAGON_POINTS + 1);
        }
    }
}
