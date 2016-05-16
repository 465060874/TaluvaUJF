package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import map.Field;

class HexShape {

    static final double HEX_SIZE_X = 60d;
    static final double HEX_SIZE_Y = 60d * 0.8d;
    static final double HEX_HEIGHT = 5d;
    static final double WEIRD_RATIO = Math.cos(Math.toRadians(30d));

    private static final int HEXAGON_POINTS = 6;
    private static final int HEXAGON_BORDER_POINTS = 5;
    private static final int BOTTOM_POINTS = 6;
    private static final int BOTTOM_BORDER_POINTS = 5;

    private final double[] hexagonX;
    private final double[] hexagonY;
    private final double[] hexagonBorderX;
    private final double[] hexagonBorderY;
    private final double[] bottomX;
    private final double[] bottomY;
    private final double[] bottomBorderX;
    private final double[] bottomBorderY;

    HexShape() {
        this.hexagonX = new double[HEXAGON_POINTS];
        this.hexagonY = new double[HEXAGON_POINTS];
        this.hexagonBorderX = new double[HEXAGON_BORDER_POINTS];
        this.hexagonBorderY = new double[HEXAGON_BORDER_POINTS];
        this.bottomX = new double[BOTTOM_POINTS];
        this.bottomY = new double[BOTTOM_POINTS];
        this.bottomBorderX = new double[BOTTOM_BORDER_POINTS];
        this.bottomBorderY = new double[BOTTOM_BORDER_POINTS];
    }

    private static Color fieldTypeColor(Field field) {
        switch (field.getType()) {
            case VOLCANO:  return Color.web("D52001");
            case JUNGLE:   return Color.web("3D9970");
            case CLEARING: return Color.web("01FF70");
            case SAND:     return Color.web("F7CA88");
            case ROCK:     return Color.web("999999");
            case LAKE:     return Color.web("0074D9");
        }

        throw new IllegalStateException();
    }

    private void update(double x, double y, double sizeX, double sizeY, double scale, Field field) {
        double hexHeight = HEX_HEIGHT * scale;
        y -= (field.getLevel() - 1) * hexHeight;
        double weirdX = sizeX * WEIRD_RATIO;
        double midY = sizeY / 2;
        double bottomDepth = hexHeight * field.getLevel();

        hexagonX[0] = x - weirdX - 0.5;
        hexagonY[0] = y + midY;

        hexagonX[1] = x;
        hexagonY[1] = y + sizeY;

        hexagonX[2] = x + weirdX;
        hexagonY[2] = y + midY;

        hexagonX[3] = x + weirdX;
        hexagonY[3] = y - midY - 0.5;

        hexagonX[4] = x;
        hexagonY[4] = y - sizeY - 0.5;

        hexagonX[5] = x - weirdX - 0.5;
        hexagonY[5] = y - midY - 0.5;

        int orientationOffset = orientationOffset(field);

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

    private int orientationOffset(Field field) {
        switch (field.getOrientation()) {
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

    private void bottomBorderLevel(double scale, int level, int maxLevel) {
        double hexHeight = HEX_HEIGHT * scale;
        double bottomDepth2 = hexHeight * (maxLevel - level + 1);
        if (level > 1) {
            bottomDepth2 -= 1;
        }

        double bottomDepth1 = bottomDepth2 - hexHeight;

        bottomBorderY[0] = hexagonY[0] + bottomDepth1;
        bottomBorderY[1] = hexagonY[0] + bottomDepth2;
        bottomBorderY[2] = hexagonY[1] + bottomDepth2;
        bottomBorderY[3] = hexagonY[2] + bottomDepth2;
        bottomBorderY[4] = hexagonY[2] + bottomDepth1;
    }

    void draw(GraphicsContext gc, Field field, boolean selected,
              double x, double y, double sizeX, double sizeY, double scale) {
        update(x, y, sizeX, sizeY, scale, field);

        Color fieldColor = fieldTypeColor(field);
        gc.setFill(selected ? fieldColor.darker() : fieldColor);
        gc.fillPolygon(
                hexagonX,
                hexagonY,
                HEXAGON_POINTS);

        gc.setFill(IslandCanvas.BOTTOM_COLOR);
        gc.fillPolygon(
                bottomX,
                bottomY,
                HEXAGON_POINTS);

        gc.setStroke(IslandCanvas.BORDER_COLOR);
        gc.strokePolyline(hexagonBorderX, hexagonBorderY, HEXAGON_BORDER_POINTS);

        gc.setStroke(IslandCanvas.BORDER_COLOR);
        for (int i = 1; i <= field.getLevel(); i++) {
            bottomBorderLevel(scale, i, field.getLevel());
            gc.strokePolyline(bottomBorderX, bottomBorderY, BOTTOM_BORDER_POINTS);
        }
    }
}