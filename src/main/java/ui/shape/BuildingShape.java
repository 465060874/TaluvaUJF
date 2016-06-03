package ui.shape;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import map.Building;
import theme.BuildingStyle;
import theme.IslandTheme;
import theme.PlayerTheme;
import ui.island.Grid;

public class BuildingShape {

    private static final float STROKE_WIDTH = 1.2f;
    public static final Image EXPAND_IMAGE = new Image("ui/shape/expand.png");

    private void drawHut(GraphicsContext gc, Grid grid,
                         double x, double y, Building building, BuildingStyle style) {
        Image hutImage = PlayerTheme.of(building.getColor()).getHutImage();
        double width = grid.getHexRadiusX() / 2;
        double height = hutImage.getHeight() / (hutImage.getWidth() / width);
        gc.drawImage(hutImage, x - width / 2, y - height / 2, width, height);

        /*double hexRadiusX = grid.getHexRadiusX();
        double hexRadiusY = grid.getHexRadiusY();
        double x1 = x - hexRadiusX / 7;
        double x2 = x;
        double x3 = x + hexRadiusX / 7;
        double y1 = y - hexRadiusY / 4;
        double y2 = y - hexRadiusY / 10;
        double y3 = y + hexRadiusY / 10;
        double y4 = y + hexRadiusY / 4;

        drawTentShape(gc, grid, x1, x2, x3, y1, y2, y3, y4, building, style);*/
    }

    private void drawTemple(GraphicsContext gc, Grid grid,
            double x, double y, Building building, BuildingStyle style) {
        double hexRadiusX = grid.getHexRadiusX();
        double hexRadiusY = grid.getHexRadiusY();
        double x1 = x - hexRadiusX / 4;
        double x2 = x;
        double x3 = x + hexRadiusX / 4;
        double y1 = y - hexRadiusY * 0.80 - hexRadiusY / 4;
        double y2 = y - hexRadiusY * 0.80 + hexRadiusY / 4;
        double y3 = y + hexRadiusY / 1.6 - hexRadiusY / 2;
        double y4 = y + hexRadiusY / 1.6;

        drawTentShape(gc, grid, x1, x2, x3, y1, y2, y3, y4, building, style);
    }

    private void drawTentShape(GraphicsContext gc, Grid grid,
            double x1, double x2, double x3, double y1, double y2, double y3, double y4,
            Building building, BuildingStyle style) {

        Paint facePaint = IslandTheme.getCurrent().getBuildingFacePaint(building, style);
        Paint topPaint = IslandTheme.getCurrent().getBuildingTopPaint(building, style);
        Effect faceEffect = IslandTheme.getCurrent().getBuildingFaceEffect(grid, building, style);
        Effect topEffect = IslandTheme.getCurrent().getBuildingTopEffect(grid, building, style);

        double[] xpoints = new double[4];
        double[] ypoints = new double[4];
        xpoints[0] = x1;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x3;
        ypoints[2] = y4;
        gc.setEffect(faceEffect);
        gc.setFill(facePaint);
        gc.fillPolygon(xpoints, ypoints, 3);
        gc.setEffect(null);
        gc.setStroke(IslandTheme.getCurrent().getBuildingBorderPaint(style));
        if (style == BuildingStyle.LASTPLACED) {
            gc.setLineWidth(STROKE_WIDTH * 3);
        } else {
            gc.setLineWidth(STROKE_WIDTH);
        }
        gc.strokePolygon(xpoints, ypoints, 3);

        xpoints[0] = x1;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x2;
        ypoints[2] = y1;
        xpoints[3] = x1;
        ypoints[3] = y2;
        gc.setEffect(topEffect);
        gc.setFill(topPaint);
        gc.fillPolygon(xpoints, ypoints, 4);
        gc.setEffect(null);
        gc.setStroke(IslandTheme.getCurrent().getBuildingBorderPaint(style));
        if (style == BuildingStyle.LASTPLACED) {
            gc.setLineWidth(STROKE_WIDTH * 3);
        } else {
            gc.setLineWidth(STROKE_WIDTH);
        }
        gc.strokePolygon(xpoints, ypoints, 4);

        xpoints[0] = x3;
        ypoints[0] = y4;
        xpoints[1] = x2;
        ypoints[1] = y3;
        xpoints[2] = x2;
        ypoints[2] = y1;
        xpoints[3] = x3;
        ypoints[3] = y2;
        gc.setEffect(topEffect);
        gc.setFill(topPaint);
        gc.fillPolygon(xpoints, ypoints, 4);
        gc.setEffect(null);
        gc.setStroke(IslandTheme.getCurrent().getBuildingBorderPaint(style));
        if (style == BuildingStyle.LASTPLACED) {
            gc.setLineWidth(STROKE_WIDTH * 3);
        } else {
            gc.setLineWidth(STROKE_WIDTH);
        }
        gc.strokePolygon(xpoints, ypoints, 4);
    }

    private void drawTower(GraphicsContext gc, Grid grid,
            double x, double y, Building building, BuildingStyle style) {
        Image towerImage = PlayerTheme.of(building.getColor()).getTowerImage();
        double width = 4 * grid.getHexRadiusX() / 5;
        double height = towerImage.getHeight() / (towerImage.getWidth() / width);
        gc.drawImage(towerImage, x - width / 2, y - height / 2, width, height);

        /*Paint facePaint = IslandTheme.getCurrent().getBuildingFacePaint(building, style);
        Paint topPaint = IslandTheme.getCurrent().getBuildingTopPaint(building, style);
        Effect faceEffect = IslandTheme.getCurrent().getBuildingFaceEffect(grid, building, style);
        Effect topEffect = IslandTheme.getCurrent().getBuildingTopEffect(grid, building, style);

        double hexRadiusX = grid.getHexRadiusX();
        double hexRadiusY = grid.getHexRadiusY();

        double width = hexRadiusX / 2;
        double xstart = x - width / 2;

        double height = hexRadiusY / 2;
        double ytop = y - hexRadiusY - hexRadiusY / 3;
        double ybottom = y - height / 2;

        gc.setEffect(faceEffect);
        gc.setFill(facePaint);
        gc.fillOval(xstart, ybottom, width, height);
        gc.setEffect(null);
        gc.setStroke(IslandTheme.getCurrent().getBuildingBorderPaint(style));
        if (style == BuildingStyle.LASTPLACED) {
            gc.setLineWidth(STROKE_WIDTH * 3);
        } else {
            gc.setLineWidth(STROKE_WIDTH);
        }
        gc.strokeOval(xstart, ybottom, width, height);

        gc.setEffect(faceEffect);
        gc.setFill(facePaint);
        gc.fillRect(xstart, ytop + height/2, width, ybottom - ytop);
        gc.setEffect(null);
        gc.setStroke(IslandTheme.getCurrent().getBuildingBorderPaint(style));
        if (style == BuildingStyle.LASTPLACED) {
            gc.setLineWidth(STROKE_WIDTH * 3);
        } else {
            gc.setLineWidth(STROKE_WIDTH);
        }
        gc.strokeLine(xstart, ytop + height/2, xstart, ybottom + height/2);
        gc.strokeLine(xstart + width, ytop + height/2, xstart + width, ybottom + height/2);

        gc.setEffect(topEffect);
        gc.setFill(topPaint);
        gc.fillOval(xstart, ytop, width, height);
        gc.setEffect(null);
        gc.setStroke(IslandTheme.getCurrent().getBuildingBorderPaint(style));
        if (style == BuildingStyle.LASTPLACED) {
            gc.setLineWidth(STROKE_WIDTH * 3);
        } else {
            gc.setLineWidth(STROKE_WIDTH);
        }
        gc.strokeOval(xstart, ytop, width, height);*/
    }

    public void draw(GraphicsContext gc, Grid grid,
                     double x, double y, int level, Building building, BuildingStyle style) {
        double hexHeight = grid.getHexHeight();
        level = Math.max(1, level);
        double y2 = y - (level - 1) * hexHeight;

        switch (building.getType()) {
            case HUT:
                if (style == BuildingStyle.FLOATING || level == 1) {
                    drawHut(gc, grid, x, y2, building, style);
                }
                else if (level == 2) {
                    drawHut(gc, grid, x - grid.getHexRadiusX() / 3, y2, building, style);
                    drawHut(gc, grid, x + grid.getHexRadiusX() / 3, y2, building, style);
                }
                else if (level == 3) {
                    double yTop = y2 - grid.getHexRadiusY() / 4;
                    drawHut(gc, grid, x - grid.getHexRadiusX() / 3, yTop, building, style);
                    drawHut(gc, grid, x + grid.getHexRadiusX() / 3, yTop, building, style);
                    drawHut(gc, grid, x, y2 + grid.getHexRadiusY() / 2, building, style);
                }
                else {
                    // TODO: More than 4
                    double yTop = y2 - grid.getHexRadiusY() / 3;
                    double yBottom = y2 + grid.getHexRadiusY() / 4;
                    double xLeft = x - grid.getHexRadiusX() / 3;
                    double xRight = x + grid.getHexRadiusX() / 3;
                    drawHut(gc, grid, xLeft, yTop, building, style);
                    drawHut(gc, grid, xRight, yTop, building, style);
                    drawHut(gc, grid, xLeft, yBottom, building, style);
                    drawHut(gc, grid, xRight, yBottom, building, style);
                }
                break;
            case TEMPLE:
                drawTemple(gc, grid, x, y2, building, style);
                break;
            case TOWER:
                drawTower(gc, grid, x, y2, building, style);
                break;
        }

        if (style == BuildingStyle.EXPAND) {
            gc.drawImage(EXPAND_IMAGE, x-34, y-24);
        }
    }
}
