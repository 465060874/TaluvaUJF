package ui;

import data.BuildingType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import map.Field;
import map.FieldBuilding;
import map.Hex;
import map.Island;

class IslandCanvas extends Canvas {

    static final Color BG_COLOR = Color.web("365373");

    private static final Paint BORDER_COLOR = Color.web("252525");
    private static final Paint BOTTOM_COLOR = Color.web("505050");

    private static final double HEX_SIZE_X = 60d;
    private static final double HEX_SIZE_Y = 60d * 0.8d;
    private static final double HEX_HEIGHT = 5d;

    private static final double WEIRD_RATIO = Math.cos(Math.toRadians(30d));

    private final Island island;
    private final boolean debug;
    private final HexBuf hexBuf;

    double ox;
    double oy;
    double scale;

    IslandCanvas(Island island, boolean debug) {
        super(0, 0);
        this.island = island;
        this.debug = debug;
        this.hexBuf = new HexBuf();

        this.ox = 0;
        this.ox = 0;
        this.scale = 1;

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();

        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

        double centerX = getWidth() / 2 - ox;
        double centerY = getHeight() / 2 - oy;
        double hexSizeY = HEX_SIZE_Y * scale;
        double hexSizeX = HEX_SIZE_X * scale;

        for (Hex hex : Hex.lineThenDiagOrdering().sortedCopy(island.getFields())) {
            drawHex(gc, hex, centerX, centerY, hexSizeY, hexSizeX);
        }

        if (debug) {
            int minLine = Integer.MAX_VALUE;
            int minDiag = Integer.MAX_VALUE;
            int maxLine = Integer.MIN_VALUE;
            int maxDiag = Integer.MIN_VALUE;

            for (Hex hex : island.getFields()) {
                minLine = Math.min(minLine, hex.getLine());
                minDiag = Math.min(minDiag, hex.getDiag());
                maxLine = Math.max(maxLine, hex.getLine());
                maxDiag = Math.max(maxDiag, hex.getDiag());
            }

            minLine -= 1;
            minDiag -= 1;
            maxLine += 1;
            maxDiag += 1;
            for (int line = minLine; line <= maxLine; line++) {
                for (int diag = minDiag; diag <= maxDiag; diag++) {
                    double x = centerX + diag * 2 * WEIRD_RATIO * hexSizeX + line * WEIRD_RATIO * hexSizeX;
                    double y = centerY + line * hexSizeY + line * hexSizeY / 2;
                    String hexStr = line + "," + diag;
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setFill(Color.WHITE);
                    gc.fillText(hexStr, x, y);
                }
            }
        }

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
    }

    private void drawHex(GraphicsContext gc, Hex hex,
                         double centerX, double centerY, double hexSizeY, double hexSizeX) {
        int line = hex.getLine();
        int diag = hex.getDiag();

        double x = centerX + diag * 2 * WEIRD_RATIO * hexSizeX + line * WEIRD_RATIO * hexSizeX;
        double y = centerY + line * hexSizeY + line * hexSizeY / 2;

        Field field = island.getField(hex);
        hexBuf.update(x, y, hexSizeX, hexSizeY, field);

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

        y -= hexSizeY / 10;
        FieldBuilding building = field.getBuilding();
        if (building.getType() != BuildingType.NONE) {
            switch (building.getType()) {
                case HUT:
                    if (building.getCount() == 1) {
                        drawHut(gc, building, x, y, hexSizeX, hexSizeY);
                    }
                    else if (building.getCount() == 2) {
                        drawHut(gc, building, x - hexSizeX / 3, y, hexSizeX, hexSizeY);
                        drawHut(gc, building, x + hexSizeX / 3, y, hexSizeX, hexSizeY);
                    }
                    else {
                        // TODO: More than 3
                        drawHut(gc, building, x - hexSizeX / 3, y - hexSizeY / 3, hexSizeX, hexSizeY);
                        drawHut(gc, building, x + hexSizeX / 3, y - hexSizeY / 3, hexSizeX, hexSizeY);
                        drawHut(gc, building, x, y + hexSizeY / 3, hexSizeX, hexSizeY);
                    }
                    break;
                case TEMPLE:
                    drawTemple(gc, building, x, y, hexSizeX, hexSizeY);
                    break;
                case TOWER:
                    drawTower(gc, building, x, y, hexSizeX, hexSizeY);
                    break;
            }
        }
    }

    private static Paint fieldTypePaint(Field field) {
        switch (field.getType()) {
            case VOLCANO:
                return Color.web("D52001");
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

    private static Paint buildingTypeFacePaint(FieldBuilding building) {
        switch (building.getColor()) {
            case RED:
                return Color.web("991f00");
            case WHITE:
                return Color.web("e6e6e6");
            case BROWN:
                return Color.web("734d26");
            case YELLOW:
                return Color.DARKGOLDENROD;
        }

        throw new IllegalStateException();
    }

    private static Paint buildingTypeTopPaint(FieldBuilding building) {
        switch (building.getColor()) {
            case RED:
                return Color.web("ff471a");
            case WHITE:
                return Color.web("f4f4f4");
            case BROWN:
                return Color.web("996633");
            case YELLOW:
                return Color.GOLDENROD;
        }

        throw new IllegalStateException();
    }

    private void drawHut(GraphicsContext gc, FieldBuilding building,
             double x, double y, double hexSizeX, double hexSizeY) {
        double x1 = x - hexSizeX / 7;
        double x2 = x;
        double x3 = x + hexSizeX / 7;
        double y1 = y - hexSizeY / 4;
        double y2 = y - hexSizeY / 10;
        double y3 = y + hexSizeY / 10;
        double y4 = y + hexSizeY / 4;

        drawTentShape(gc, building, x1, x2, x3, y1, y2, y3, y4);
    }

    private void drawTemple(GraphicsContext gc, FieldBuilding building,
            double x, double y, double hexSizeX, double hexSizeY) {
        double x1 = x - hexSizeX / 4;
        double x2 = x;
        double x3 = x + hexSizeX / 4;
        double y1 = y - hexSizeY * 0.80 - hexSizeY / 4;
        double y2 = y - hexSizeY * 0.80 + hexSizeY / 4;
        double y3 = y + hexSizeY / 1.6 - hexSizeY / 2;
        double y4 = y + hexSizeY / 1.6;

        drawTentShape(gc, building, x1, x2, x3, y1, y2, y3, y4);
    }

    private void drawTentShape(GraphicsContext gc, FieldBuilding building,
           double x1, double x2, double x3, double y1, double y2, double y3, double y4) {
        double[] xpoints = new double[4];
        double[] ypoints = new double[4];
        xpoints[0] = x1;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x3;
        ypoints[2] = y4;
        gc.setFill(buildingTypeFacePaint(building));
        gc.fillPolygon(xpoints, ypoints, 3);
        gc.setStroke(BORDER_COLOR);
        gc.strokePolygon(xpoints, ypoints, 3);

        xpoints[0] = x1;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x2;
        ypoints[2] = y1;
        xpoints[3] = x1;
        ypoints[3] = y2;
        gc.setFill(buildingTypeTopPaint(building));
        gc.fillPolygon(xpoints, ypoints, 4);
        gc.setStroke(BORDER_COLOR);
        gc.strokePolygon(xpoints, ypoints, 4);

        xpoints[0] = x3;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x2;
        ypoints[2] = y1;
        xpoints[3] = x3;
        ypoints[3] = y2;
        gc.setFill(buildingTypeTopPaint(building));
        gc.fillPolygon(xpoints, ypoints, 4);
        gc.setStroke(BORDER_COLOR);
        gc.strokePolygon(xpoints, ypoints, 4);
    }

    private void drawTower(GraphicsContext gc, FieldBuilding building,
           double x, double y, double hexSizeX, double hexSizeY) {
        double width = hexSizeX / 2;
        double xstart = x - width / 2;

        double height = hexSizeY / 2;
        double ytop = y - hexSizeY - hexSizeY / 3;
        double ybottom = y - height / 2;

        gc.setFill(buildingTypeFacePaint(building));
        gc.fillOval(xstart, ybottom, width, height);
        gc.setStroke(BORDER_COLOR);
        gc.strokeOval(xstart, ybottom, width, height);

        gc.setFill(buildingTypeFacePaint(building));
        gc.fillRect(xstart, ytop + height/2, width, ybottom - ytop);
        gc.setStroke(BORDER_COLOR);
        gc.strokeLine(xstart, ytop + height/2, xstart, ybottom + height/2);
        gc.strokeLine(xstart + width, ytop + height/2, xstart + width, ybottom + height/2);

        gc.setFill(buildingTypeTopPaint(building));
        gc.fillOval(xstart, ytop, width, height);
        gc.setStroke(BORDER_COLOR);
        gc.strokeOval(xstart, ytop, width, height);
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

        void update(double x, double y, double sizeX, double sizeY, Field field) {
            double hexHeight = HEX_HEIGHT * scale;
            y -= (field.getLevel() - 1) * hexHeight;
            double weirdX = sizeX * WEIRD_RATIO;
            double weirdY = sizeY / 2;
            double bottomDepth = hexHeight * field.getLevel();

            hexagonX[0] = x - weirdX - 0.5;
            hexagonY[0] = y + weirdY;

            hexagonX[1] = x;
            hexagonY[1] = y + sizeY;

            hexagonX[2] = x + weirdX;
            hexagonY[2] = y + weirdY;

            hexagonX[3] = x + weirdX;
            hexagonY[3] = y - weirdY - 0.5;

            hexagonX[4] = x;
            hexagonY[4] = y - sizeY - 0.5;

            hexagonX[5] = x - weirdX - 0.5;
            hexagonY[5] = y - weirdY - 0.5;

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
            double hexHeight = HEX_HEIGHT * scale;
            double bottomDepth2 = hexHeight * level;
            double bottomDepth1 = bottomDepth2 - hexHeight;

            bottomBorderY[0] = hexagonY[0] + bottomDepth1;
            bottomBorderY[1] = hexagonY[0] + bottomDepth2;
            bottomBorderY[2] = hexagonY[1] + bottomDepth2;
            bottomBorderY[3] = hexagonY[2] + bottomDepth2;
            bottomBorderY[4] = hexagonY[2] + bottomDepth1;
        }
    }
}
