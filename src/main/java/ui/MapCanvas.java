package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

class MapCanvas extends Canvas {

    private static final Color BG_COLOR = Color.web("7FDBFF");

    private static final Color VOLCANO_COLOR = Color.web("FF4136");
    private static final Color LAKE_COLOR = Color.web("0074D9");
    private static final Color JUNGLE_COLOR = Color.web("01FF70");

    public static final double HEX_SIZE_X = 60;
    public static final double HEX_SIZE_Y = 60 * 0.75;
    public static final double WEIRD_RATIO = Math.cos(Math.PI / 180 * (double) 30);

    private final HexBuf hexBuf;

    MapCanvas() {
        super(0, 0);
        this.hexBuf = new HexBuf();

        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public void redraw() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, getWidth(), getHeight());

        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;

        Random random = new Random();
        for (int d = -3; d <= 3; d++) {
            for (int l = -3; l <= 3; l++) {
                if (Math.abs(l + d) > 3) {
                    continue;
                }

                double x = centerX + d * 2 * WEIRD_RATIO * HEX_SIZE_X + l * WEIRD_RATIO * HEX_SIZE_X;
                double y = centerY + l * HEX_SIZE_Y + l * HEX_SIZE_Y / 2;

                hexBuf.update(x, y, HEX_SIZE_X, HEX_SIZE_Y);
                switch (random.nextInt(3)) {
                    case 0:
                        gc.setFill(JUNGLE_COLOR);
                        break;
                    case 1:
                        gc.setFill(LAKE_COLOR);
                        break;
                    case 2:
                        gc.setFill(VOLCANO_COLOR);
                        break;
                }

                gc.fillPolygon(hexBuf.xPoints, hexBuf.yPoints, HexBuf.N_POINTS);
                gc.setFill(Color.BLACK);
                gc.strokePolygon(hexBuf.xPoints, hexBuf.yPoints, HexBuf.N_POINTS);
            }
        }
    }

    private static class HexBuf {

        private static final int N_POINTS = 6;

        private final double[] xPoints;
        private final double[] yPoints;

        private HexBuf() {
            this.xPoints = new double[N_POINTS];
            this.yPoints = new double[N_POINTS];
        }

        void update(double centerX, double centerY, double sizeX, double sizeY) {
            xPoints[0] = centerX;
            yPoints[0] = centerY + sizeY;

            xPoints[1] = centerX + sizeX * WEIRD_RATIO;
            yPoints[1] = centerY + sizeY / 2;

            xPoints[2] = centerX + sizeX * WEIRD_RATIO;
            yPoints[2] = centerY - sizeY / 2;

            xPoints[3] = centerX;
            yPoints[3] = centerY - sizeY;

            xPoints[4] = centerX - sizeX * WEIRD_RATIO;
            yPoints[4] = centerY - sizeY / 2;

            xPoints[5] = centerX - sizeX * WEIRD_RATIO;
            yPoints[5] = centerY + sizeY / 2;
        }
    }
}
