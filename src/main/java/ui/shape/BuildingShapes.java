package ui.shape;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import map.FieldBuilding;
import ui.theme.PlacementState;
import ui.theme.Theme;

import static ui.island.Grid.HEX_HEIGHT;

public class BuildingShapes {

    private static final float STROKE_WIDTH = 1.2f;

    public static void drawBuilding(GraphicsContext gc,
            FieldBuilding building, int level, PlacementState placementState,
             double scale, double x, double y, double hexSizeX, double hexSizeY) {
        double hexHeight = HEX_HEIGHT * scale;
        y -= (level - 1) * hexHeight;

        switch (building.getType()) {
            case HUT:
                if (level == 1) {
                    drawHut(gc, building, placementState, x, y, hexSizeX, hexSizeY);
                } else if (level == 2) {
                    drawHut(gc, building, placementState,
                            x - hexSizeX / 3, y, hexSizeX, hexSizeY);
                    drawHut(gc, building, placementState,
                            x + hexSizeX / 3, y, hexSizeX, hexSizeY);
                } else {
                    // TODO: More than 3
                    drawHut(gc, building, placementState,
                            x - hexSizeX / 3, y - hexSizeY / 4, hexSizeX, hexSizeY);
                    drawHut(gc, building, placementState,
                            x + hexSizeX / 3, y - hexSizeY / 4, hexSizeX, hexSizeY);
                    drawHut(gc, building, placementState,
                            x, y + hexSizeY / 2, hexSizeX, hexSizeY);
                }
                break;
            case TEMPLE:
                drawTemple(gc, building, placementState, x, y, hexSizeX, hexSizeY);
                break;
            case TOWER:
                drawTower(gc, building, placementState, x, y, hexSizeX, hexSizeY);
                break;
        }
    }

    private static void drawHut(GraphicsContext gc,
            FieldBuilding building, PlacementState placementState,
            double x, double y, double hexSizeX, double hexSizeY) {
        double x1 = x - hexSizeX / 7;
        double x2 = x;
        double x3 = x + hexSizeX / 7;
        double y1 = y - hexSizeY / 4;
        double y2 = y - hexSizeY / 10;
        double y3 = y + hexSizeY / 10;
        double y4 = y + hexSizeY / 4;

        drawTentShape(gc, building, placementState, x1, x2, x3, y1, y2, y3, y4);
    }

    private static void drawTemple(GraphicsContext gc,
            FieldBuilding building,
            PlacementState placementState,
            double x, double y, double hexSizeX, double hexSizeY) {
        double x1 = x - hexSizeX / 4;
        double x2 = x;
        double x3 = x + hexSizeX / 4;
        double y1 = y - hexSizeY * 0.80 - hexSizeY / 4;
        double y2 = y - hexSizeY * 0.80 + hexSizeY / 4;
        double y3 = y + hexSizeY / 1.6 - hexSizeY / 2;
        double y4 = y + hexSizeY / 1.6;

        drawTentShape(gc, building, placementState, x1, x2, x3, y1, y2, y3, y4);
    }

    private static void drawTentShape(GraphicsContext gc,
            FieldBuilding building, PlacementState placementState,
            double x1, double x2, double x3, double y1, double y2, double y3, double y4) {
        Paint facePaint = Theme.getCurrent().get().getBuildingFacePaint(building, placementState);
        Paint topPaint = Theme.getCurrent().get().getBuildingTopPaint(building, placementState);

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
        gc.setStroke(Theme.getCurrent().get().getBuildingBorderPaint());
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
        gc.setStroke(Theme.getCurrent().get().getBuildingBorderPaint());
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
        gc.setStroke(Theme.getCurrent().get().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokePolygon(xpoints, ypoints, 4);
    }

    private static void drawTower(GraphicsContext gc, FieldBuilding building, PlacementState placementState,
              double x, double y, double hexSizeX, double hexSizeY) {
        Paint facePaint = Theme.getCurrent().get().getBuildingFacePaint(building, placementState);
        Paint topPaint = Theme.getCurrent().get().getBuildingTopPaint(building, placementState);

        double width = hexSizeX / 2;
        double xstart = x - width / 2;

        double height = hexSizeY / 2;
        double ytop = y - hexSizeY - hexSizeY / 3;
        double ybottom = y - height / 2;

        gc.setFill(facePaint);
        gc.fillOval(xstart, ybottom, width, height);
        gc.setStroke(Theme.getCurrent().get().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokeOval(xstart, ybottom, width, height);

        gc.setFill(facePaint);
        gc.fillRect(xstart, ytop + height/2, width, ybottom - ytop);
        gc.setStroke(Theme.getCurrent().get().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokeLine(xstart, ytop + height/2, xstart, ybottom + height/2);
        gc.strokeLine(xstart + width, ytop + height/2, xstart + width, ybottom + height/2);

        gc.setFill(topPaint);
        gc.fillOval(xstart, ytop, width, height);
        gc.setStroke(Theme.getCurrent().get().getBuildingBorderPaint());
        gc.setLineWidth(STROKE_WIDTH);
        gc.strokeOval(xstart, ytop, width, height);
    }
}
