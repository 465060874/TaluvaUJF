package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Random;

class MapCanvas extends Canvas {

    private static final Color BG_COLOR = Color.web("365373");

    private static final Paint BORDER_COLOR = Color.web("252525");
    private static final Paint BORDER_COLOR2 = Color.web("505050");

    private static final Color[] FIELD_TYPE_COLORS = new Color[] {
            Color.web("FF4136"), // VOLCANO
            Color.web("0074D9"), // LAKE
            Color.web("01FF70"), // CLEARING
            Color.web("3D9970"), // JUNGLE
            Color.web("999999"), // ROCK
            Color.web("F7CA88"), // SAND
    };

    private static final double HEX_SIZE_X = 60d;
    private static final double HEX_SIZE_Y = 60d * 0.8d;
    private static final double HEX_HEIGHT = 5d;

    private static final double WEIRD_RATIO = Math.cos(Math.PI / 180d * 30d);

    private final int[][] levels;
    private final Color[][] fieldColors;

    private final HexBuf hexBuf;

    MapCanvas() {
        super(0, 0);
        this.levels = new int[9][9];
        this.fieldColors = new Color[9][9];
        Random random = new Random();
        for (int l = -4; l <= 4; l++) {
            for (int d = -4; d <= 4; d++) {
                if (Math.abs(l + d) > 4) {
                    continue;
                }

                double randLevel = random.nextDouble();
                if (randLevel > 0.95) {
                    levels[l + 4][d + 4] = 4;
                }
                else if (randLevel > 0.85) {
                    levels[l + 4][d + 4] = 3;
                }
                else if (randLevel > 0.65) {
                    levels[l + 4][d + 4] = 2;
                }
                else {
                    levels[l + 4][d + 4] = 1;
                }

                fieldColors[l + 4][d + 4] = FIELD_TYPE_COLORS[random.nextInt(FIELD_TYPE_COLORS.length)];
            }
        }

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

        for (int l = -4; l <= 4; l++) {
            for (int d = -4; d <= 4; d++) {
                if (Math.abs(l + d) > 4) {
                    continue;
                }

                double x = centerX + d * 2 * WEIRD_RATIO * HEX_SIZE_X + l * WEIRD_RATIO * HEX_SIZE_X;
                double y = centerY + l * hexSizeY + l * hexSizeY / 2;

                int level = levels[l + 4][d + 4];

                for (int i = 1; i <= level; i++) {
                    if (i == level) {
                        hexBuf.hexagon(x, y, HEX_SIZE_X, hexSizeY, level);
                        gc.setFill(fieldColors[l + 4][d + 4]);
                        gc.fillPolygon(
                                hexBuf.hexagonsX,
                                hexBuf.hexagonsY,
                                HexBuf.HEXAGON_POINTS);
                    }

                    hexBuf.bottom(x, y, HEX_SIZE_X, hexSizeY, i);
                    gc.setFill(BORDER_COLOR2);
                    gc.fillPolygon(hexBuf.bottomX, hexBuf.bottomY, HexBuf.HEXAGON_POINTS);

                    gc.setStroke(BORDER_COLOR);
                    gc.strokePolygon(hexBuf.hexagonsX, hexBuf.hexagonsY, HexBuf.HEXAGON_POINTS);
                    gc.strokePolygon(hexBuf.bottomX, hexBuf.bottomY, HexBuf.BOTTOM_POINTS);
                }
            }
        }
    }

    private class HexBuf {

        private static final int HEXAGON_POINTS = 6;
        private static final int BOTTOM_POINTS = 6;

        private final double[] hexagonsX;
        private final double[] hexagonsY;
        private final double[] bottomX;
        private final double[] bottomY;

        private HexBuf() {
            this.hexagonsX = new double[HEXAGON_POINTS];
            this.hexagonsY = new double[HEXAGON_POINTS];
            this.bottomX = new double[BOTTOM_POINTS];
            this.bottomY = new double[BOTTOM_POINTS];
        }

        void hexagon(double centerX, double centerY, double sizeX, double sizeY, int level) {
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
        }

        void bottom(double centerX, double centerY, double sizeX, double sizeY, int level) {
            centerY -= (level - 1) * HEX_HEIGHT;

            bottomX[0] = centerX - sizeX * WEIRD_RATIO;
            bottomY[0] = centerY + sizeY / 2;

            bottomX[1] = centerX;
            bottomY[1] = centerY + sizeY;

            bottomX[2] = centerX + sizeX * WEIRD_RATIO;
            bottomY[2] = centerY + sizeY / 2;

            bottomX[3] = bottomX[2];
            bottomY[3] = bottomY[2] + HEX_HEIGHT;

            bottomX[4] = bottomX[1];
            bottomY[4] = bottomY[1] + HEX_HEIGHT;

            bottomX[5] = bottomX[0];
            bottomY[5] = bottomY[0] + HEX_HEIGHT;
        }
    }
}
