package ui.shape;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import ui.island.Grid;
import ui.theme.PlacementState;
import ui.theme.Theme;

public class BuildingShapes {

    private static final float STROKE_WIDTH = 1.2f;

    public static void drawBuilding(GraphicsContext gc, Grid grid, HexShapeInfo info) {
        double hexHeight = grid.getHexHeight();
        int level = Math.max(1, info.level);
        double y = info.y - (level - 1) * hexHeight;

        switch (info.building.getType()) {
            case HUT:
                if (info.placementState == PlacementState.INVALID || level == 1) {
                    drawHut(gc, grid, info, info.x, y);
                } else if (level == 2) {
                    drawHut(gc, grid, info, info.x - grid.getHexRadiusX() / 3, y);
                    drawHut(gc, grid, info, info.x + grid.getHexRadiusX() / 3, y);
                } else {
                    // TODO: More than 3
                    drawHut(gc, grid, info, info.x - grid.getHexRadiusX() / 3, y - grid.getHexRadiusY() / 4);
                    drawHut(gc, grid, info, info.x + grid.getHexRadiusX() / 3, y - grid.getHexRadiusY() / 4);
                    drawHut(gc, grid, info, info.x, y + grid.getHexRadiusY() / 2);
                }
                break;
            case TEMPLE:
                drawTemple(gc, grid, info, info.x, y);
                break;
            case TOWER:
                drawTower(gc, grid, info, info.x, y);
                break;
        }
    }

    private static void drawHut(GraphicsContext gc, Grid grid, HexShapeInfo info, double x, double y) {
        double hexRadiusX = grid.getHexRadiusX();
        double hexRadiusY = grid.getHexRadiusY();
        double x1 = x - hexRadiusX / 7;
        double x2 = x;
        double x3 = x + hexRadiusX / 7;
        double y1 = y - hexRadiusY / 4;
        double y2 = y - hexRadiusY / 10;
        double y3 = y + hexRadiusY / 10;
        double y4 = y + hexRadiusY / 4;

        drawTentShape(gc, info, x1, x2, x3, y1, y2, y3, y4);
    }

    private static void drawTemple(GraphicsContext gc, Grid grid, HexShapeInfo info, double x, double y) {
        double hexRadiusX = grid.getHexRadiusX();
        double hexRadiusY = grid.getHexRadiusY();
        double x1 = x - hexRadiusX / 4;
        double x2 = x;
        double x3 = x + hexRadiusX / 4;
        double y1 = y - hexRadiusY * 0.80 - hexRadiusY / 4;
        double y2 = y - hexRadiusY * 0.80 + hexRadiusY / 4;
        double y3 = y + hexRadiusY / 1.6 - hexRadiusY / 2;
        double y4 = y + hexRadiusY / 1.6;

        drawTentShape(gc, info, x1, x2, x3, y1, y2, y3, y4);
    }

    private static void drawTentShape(GraphicsContext gc, HexShapeInfo info,
            double x1, double x2, double x3, double y1, double y2, double y3, double y4) {
        Paint facePaint = Theme.getCurrent().getBuildingFacePaint(info.building, info.placementState);
        Paint topPaint = Theme.getCurrent().getBuildingTopPaint(info.building, info.placementState);

        double[] xpoints = new double[4];
        double[] ypoints = new double[4];
        xpoints[0] = x1;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x3;
        ypoints[2] = y4;
        gc.setFill(facePaint);
        gc.fillPolygon(xpoints, ypoints, 3);
        gc.setStroke(Theme.getCurrent().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokePolygon(xpoints, ypoints, 3);

        xpoints[0] = x1;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x2;
        ypoints[2] = y1;
        xpoints[3] = x1;
        ypoints[3] = y2;
        gc.setFill(topPaint);
        gc.fillPolygon(xpoints, ypoints, 4);
        gc.setStroke(Theme.getCurrent().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokePolygon(xpoints, ypoints, 4);

        xpoints[0] = x3;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x2;
        ypoints[2] = y1;
        xpoints[3] = x3;
        ypoints[3] = y2;
        gc.setFill(topPaint);
        gc.fillPolygon(xpoints, ypoints, 4);
        gc.setStroke(Theme.getCurrent().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokePolygon(xpoints, ypoints, 4);
    }

    private static void drawTower(GraphicsContext gc, Grid grid, HexShapeInfo info, double x, double y) {
        Paint facePaint = Theme.getCurrent().getBuildingFacePaint(info.building, info.placementState);
        Paint topPaint = Theme.getCurrent().getBuildingTopPaint(info.building, info.placementState);

        double hexRadiusX = grid.getHexRadiusX();
        double hexRadiusY = grid.getHexRadiusY();

        double width = hexRadiusX / 2;
        double xstart = x - width / 2;

        double height = hexRadiusY / 2;
        double ytop = y - hexRadiusY - hexRadiusY / 3;
        double ybottom = y - height / 2;

        gc.setFill(facePaint);
        gc.fillOval(xstart, ybottom, width, height);
        gc.setStroke(Theme.getCurrent().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokeOval(xstart, ybottom, width, height);

        gc.setFill(facePaint);
        gc.fillRect(xstart, ytop + height/2, width, ybottom - ytop);
        gc.setStroke(Theme.getCurrent().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokeLine(xstart, ytop + height/2, xstart, ybottom + height/2);
        gc.strokeLine(xstart + width, ytop + height/2, xstart + width, ybottom + height/2);

        gc.setFill(topPaint);
        gc.fillOval(xstart, ytop, width, height);
        gc.setStroke(Theme.getCurrent().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokeOval(xstart, ytop, width, height);
    }
}
