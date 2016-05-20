package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import map.Field;

class HexShape {

    static final double HEX_SIZE_X = 60d;
    static final double HEX_SIZE_Y = 60d * 0.8d;
    static final double HEX_HEIGHT = 5d;
    static final double WEIRD_RATIO = Math.cos(Math.toRadians(30d));

    private static final int HEXAGON_POINTS = 6;
    private static final int HEXAGON_BORDER_POINTS = 5;
    private static final int HEXAGON_BORDER2_POINTS = 3;
    private static final int BOTTOM_POINTS = 6;
    private static final int BOTTOM_BORDER_POINTS = 5;
    public static final float STROKE_WIDTH = 2f;

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

    HexShape() {
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
    }

    private static Color fieldTypeColor(Field field) {
        switch (field.getType()) {
            case VOLCANO:  return Color.web("E97B33");
            case JUNGLE:   return Color.web("A681B6");
            case CLEARING: return Color.web("8DC435");
            case SAND:     return Color.web("EFDD6F");
            case ROCK:     return Color.web("C2D0D1");
            case LAKE:     return Color.web("8BE1EB");
        }

        throw new IllegalStateException();
    }

    private void update(HexShapeInfo info) {
        double hexHeight = HEX_HEIGHT * info.scale;
        double x = info.x;
        double y = info.y - (info.field.getLevel() - 1) * hexHeight;
        double weirdX = info.sizeX * WEIRD_RATIO;
        double midY = info.sizeY / 2;
        double bottomDepth = hexHeight * info.field.getLevel();

        hexagonX[0] = x - weirdX;
        hexagonY[0] = y + midY;

        hexagonX[1] = x;
        hexagonY[1] = y + info.sizeY;

        hexagonX[2] = x + weirdX;
        hexagonY[2] = y + midY;

        hexagonX[3] = x + weirdX;
        hexagonY[3] = y - midY;

        hexagonX[4] = x;
        hexagonY[4] = y - info.sizeY;

        hexagonX[5] = x - weirdX;
        hexagonY[5] = y - midY;

        int orientationOffset = orientationOffset(info.field);

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
        double bottomDepth1 = bottomDepth2 - hexHeight;
        if (level > 1) {
            bottomDepth2 -= 1;
        }
        if (level == maxLevel) {
            bottomDepth1 += scale;
        }


        bottomBorderY[0] = hexagonY[0] + bottomDepth1;
        bottomBorderY[1] = hexagonY[0] + bottomDepth2;
        bottomBorderY[2] = hexagonY[1] + bottomDepth2;
        bottomBorderY[3] = hexagonY[2] + bottomDepth2;
        bottomBorderY[4] = hexagonY[2] + bottomDepth1;
    }

    private static Color fieldTypeTranslucentColor(Field field) {
        Color color = fieldTypeColor(field);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), .5f);
    }

    void draw(GraphicsContext gc, HexShapeInfo info) {
        update(info);

        gc.setEffect(new Lighting(new Light.Point(0, 0, info.scale * 100, Color.WHITE)));
        gc.setFill(info.isPlacement
                ? fieldTypeTranslucentColor(info.field)
                : fieldTypeColor(info.field));
        gc.fillPolygon(hexagonX, hexagonY, HEXAGON_POINTS);
        gc.setEffect(null);

        gc.setFill(info.isPlacement
                ? IslandCanvas.BORDER_COLOR.deriveColor(1, 1, 1, .5f)
                : IslandCanvas.BOTTOM_COLOR);
        gc.fillPolygon(bottomX, bottomY, HEXAGON_POINTS);

        gc.setStroke(IslandCanvas.BORDER_COLOR);
        gc.setLineWidth(STROKE_WIDTH * info.scale);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokePolyline(hexagonBorderX, hexagonBorderY, HEXAGON_BORDER_POINTS);

        gc.setStroke(IslandCanvas.BORDER_COLOR);
        gc.setLineWidth(STROKE_WIDTH * info.scale);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        for (int i = 1; i <= info.field.getLevel(); i++) {
            bottomBorderLevel(info.scale, i, info.field.getLevel());
            gc.strokePolyline(bottomBorderX, bottomBorderY, BOTTOM_BORDER_POINTS);
        }
    }
}
