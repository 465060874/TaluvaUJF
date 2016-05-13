package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Random;

class MapCanvas extends Canvas {

    private static final Color BG_COLOR = Color.web("7FDBFF");

    private static final Paint BORDER_COLOR = Color.web("252525");
    private static final Color[] FIELD_TYPE_COLORS = new Color[] {
            Color.web("FF4136"), // VOLCANO
            Color.web("0074D9"), // LAKE
            Color.web("01FF70"), // CLEARING
            Color.web("3D9970"), // JUNGLE
            Color.web("999999"), // ROCK
            Color.web("DBD1B4"), // SAND
    };

    private static final double HEX_SIZE_X = 60d;
    private static final double HEX_SIZE_Y = 60d * 0.75d;
    private static final double HEX_HEIGHT = 7d;

    private static final double WEIRD_RATIO = Math.cos(Math.PI / 180d * 30d);

    private final HexBuf hexBuf;

    MapCanvas() {
        super(0, 0);
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

        Random random = new Random();
        for (int l = -3; l <= 3; l++) {
            for (int d = -3; d <= 3; d++) {
                if (Math.abs(l + d) > 3) {
                    continue;
                }

                double x = centerX + d * 2 * WEIRD_RATIO * HEX_SIZE_X + l * WEIRD_RATIO * HEX_SIZE_X;
                double y = centerY + l * HEX_SIZE_Y + l * HEX_SIZE_Y / 2;

                int level = 1;
                if (l == 0 && d == 0) {
                    level = 2;
                }
                else if (l == -1 && (d == 0 || d == 1)) {
                    level = 3;
                }
                else if (l == -2 && (d == 0 || d == 1 || d == 2)) {
                    level = d == 2 ? 2 : 4;
                }

                gc.setFill(FIELD_TYPE_COLORS[random.nextInt(FIELD_TYPE_COLORS.length)]);
                hexBuf.update(x, y, HEX_SIZE_X, HEX_SIZE_Y, level);
                gc.fillPolygon(hexBuf.hexagonsX, hexBuf.hexagonsY, HexBuf.HEXAGON_POINTS);
                gc.setFill(BORDER_COLOR);
                gc.strokePolygon(hexBuf.hexagonsX, hexBuf.hexagonsY, HexBuf.HEXAGON_POINTS);
                gc.fillPolygon(hexBuf.bottomX, hexBuf.bottomY, HexBuf.BOTTOM_POINTS);
            }
        }
    }

    private static class HexBuf {

        private static final int HEXAGON_POINTS = 6;
        private static final int BOTTOM_POINTS = 6;

        private final double[] hexagonsX;
        private final double[] hexagonsY;
        private final double[] bottomX;
        private final double[] bottomY;

        private HexBuf() {
            this.hexagonsX = new double[HEXAGON_POINTS];
            this.hexagonsY = new double[HEXAGON_POINTS];
            this.bottomX = new double[HEXAGON_POINTS];
            this.bottomY = new double[BOTTOM_POINTS];
        }

        void update(double centerX, double centerY, double sizeX, double sizeY, int level) {
            centerY -= (level - 1) * HEX_HEIGHT;

            hexagonsX[0] = centerX - sizeX * WEIRD_RATIO;
            hexagonsY[0] = centerY + sizeY / 2;

            hexagonsX[1] = centerX;
            hexagonsY[1] = centerY + sizeY;

            hexagonsX[2] = centerX + sizeX * WEIRD_RATIO;
            hexagonsY[2] = centerY + sizeY / 2;

            hexagonsX[3] = centerX + sizeX * WEIRD_RATIO;
            hexagonsY[3] = centerY - sizeY / 2;

            hexagonsX[4] = centerX;
            hexagonsY[4] = centerY - sizeY;

            hexagonsX[5] = centerX - sizeX * WEIRD_RATIO;
            hexagonsY[5] = centerY - sizeY / 2;

            bottomX[0] = hexagonsX[0];
            bottomY[0] = hexagonsY[0];

            bottomX[1] = hexagonsX[1];
            bottomY[1] = hexagonsY[1];

            bottomX[2] = hexagonsX[2];
            bottomY[2] = hexagonsY[2];

            bottomX[3] = hexagonsX[2];
            bottomY[3] = hexagonsY[2] + HEX_HEIGHT * level;

            bottomX[4] = hexagonsX[1];
            bottomY[4] = hexagonsY[1] + HEX_HEIGHT * level;

            bottomX[5] = hexagonsX[0];
            bottomY[5] = hexagonsY[0] + HEX_HEIGHT * level;
        }
    }
}
