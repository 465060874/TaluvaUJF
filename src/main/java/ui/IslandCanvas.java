package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import map.Field;
import map.Hex;
import map.Island;

class IslandCanvas extends Canvas {

    private static final Color BG_COLOR = Color.web("365373");

    private static final Paint BORDER_COLOR = Color.web("252525");
    private static final Paint BOTTOM_COLOR = Color.web("505050");

    private static final double HEX_SIZE_X = 60d;
    private static final double HEX_SIZE_Y = 60d * 0.8d;
    private static final double HEX_HEIGHT = 5d;

    private static final double WEIRD_RATIO = Math.cos(Math.toRadians(30d));

    private final Island island;
    private final boolean debug;
    private final HexBuf hexBuf;

    IslandCanvas(Island island, boolean debug) {
        super(0, 0);
        this.island = island;
        this.debug = debug;
        this.hexBuf = new HexBuf();

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());

        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;
        double hexSizeY = HEX_SIZE_Y;

        int minLine = Integer.MAX_VALUE;
        int minDiag = Integer.MAX_VALUE;
        int maxLine = Integer.MIN_VALUE;
        int maxDiag = Integer.MIN_VALUE;

        for (Hex hex : Hex.lineThenDiagOrdering().sortedCopy(island.getFields())) {
            int line = hex.getLine();
            int diag = hex.getDiag();

            if (debug) {
                minLine = Math.min(minLine, line);
                minDiag = Math.min(minDiag, diag);
                maxLine = Math.max(maxLine, line);
                maxDiag = Math.max(maxDiag, diag);
            }

            double x = centerX + diag * 2 * WEIRD_RATIO * HEX_SIZE_X + line * WEIRD_RATIO * HEX_SIZE_X;
            double y = centerY + line * hexSizeY + line * hexSizeY / 2;

            Field field = island.getField(hex);
            hexBuf.update(x, y, HEX_SIZE_X, hexSizeY, field);

            gc.setFill(fieldTypePaint(field));
            gc.fillPolygon(
                    hexBuf.hexagonX,
                    hexBuf.hexagonY,
                    HexBuf.HEXAGON_POINTS);

            gc.setFill(BOTTOM_COLOR);
            gc.fillPolygon(
                    hexBuf.bottomX,
                    hexBuf.bottomY,
                    HexBuf.HEXAGON_POINTS);

            gc.setStroke(BORDER_COLOR);
            gc.strokePolyline(hexBuf.hexagonBorderX, hexBuf.hexagonBorderY, HexBuf.HEXAGON_BORDER_POINTS);

            gc.setStroke(BORDER_COLOR);
            for (int i = 1; i <= field.getLevel(); i++) {
                hexBuf.bottomBorderLevel(i);
                gc.strokePolyline(hexBuf.bottomBorderX, hexBuf.bottomBorderY, HexBuf.BOTTOM_BORDER_POINTS);
            }

        }

        if (debug) {
            minLine -= 1;
            minDiag -= 1;
            maxLine += 1;
            maxDiag += 1;
            for (int line = minLine; line <= maxLine; line++) {
                for (int diag = minDiag; diag <= maxDiag; diag++) {
                    double x = centerX + diag * 2 * WEIRD_RATIO * HEX_SIZE_X + line * WEIRD_RATIO * HEX_SIZE_X;
                    double y = centerY + line * hexSizeY + line * hexSizeY / 2;
                    String hexStr = line + "," + diag;
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setFill(Color.WHITE);
                    gc.fillText(hexStr, x, y);
                }
            }
        }
    }

    private static Paint fieldTypePaint(Field field) {
        switch (field.getType()) {
            case VOLCANO:
                return Color.web("FF4136");
            case JUNGLE:
                return Color.web("3D9970");
            case CLEARING:
                return Color.web("01FF70");
            case SAND:
                return Color.web("F7CA88");
            case ROCK:
                return Color.web("999999");
            case LAKE:
                return Color.web("0074D9");
        }

        throw new IllegalStateException();
    }

    private class HexBuf {

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

        private HexBuf() {
            this.hexagonX = new double[HEXAGON_POINTS];
            this.hexagonY = new double[HEXAGON_POINTS];
            this.hexagonBorderX = new double[HEXAGON_BORDER_POINTS];
            this.hexagonBorderY = new double[HEXAGON_BORDER_POINTS];
            this.bottomX = new double[BOTTOM_POINTS];
            this.bottomY = new double[BOTTOM_POINTS];
            this.bottomBorderX = new double[BOTTOM_BORDER_POINTS];
            this.bottomBorderY = new double[BOTTOM_BORDER_POINTS];
        }

        void update(double centerX, double centerY, double sizeX, double sizeY, Field field) {
            centerY -= (field.getLevel() - 1) * HEX_HEIGHT;
            double weirdX = sizeX * WEIRD_RATIO;
            double weirdY = sizeY / 2;
            double bottomDepth = HEX_HEIGHT * field.getLevel();

            hexagonX[0] = centerX - weirdX - 0.5;
            hexagonY[0] = centerY + weirdY;

            hexagonX[1] = centerX;
            hexagonY[1] = centerY + sizeY;

            hexagonX[2] = centerX + weirdX;
            hexagonY[2] = centerY + weirdY;

            hexagonX[3] = centerX + weirdX;
            hexagonY[3] = centerY - weirdY - 0.5;

            hexagonX[4] = centerX;
            hexagonY[4] = centerY - sizeY - 0.5;

            hexagonX[5] = centerX - weirdX - 0.5;
            hexagonY[5] = centerY - weirdY - 0.5;

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

        void bottomBorderLevel(int level) {
            double bottomDepth2 = HEX_HEIGHT * level;
            double bottomDepth1 = bottomDepth2 - HEX_HEIGHT;

            bottomBorderY[0] = hexagonY[0] + bottomDepth1;
            bottomBorderY[1] = hexagonY[0] + bottomDepth2;
            bottomBorderY[2] = hexagonY[1] + bottomDepth2;
            bottomBorderY[3] = hexagonY[2] + bottomDepth2;
            bottomBorderY[4] = hexagonY[2] + bottomDepth1;
        }
    }
}
