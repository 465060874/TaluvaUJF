package ui.shape;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import map.Building;
import theme.BuildingStyle;
import theme.PlayerTheme;
import ui.island.Grid;

public class BuildingShape {

    public static final Image EXPAND_IMAGE = new Image("ui/shape/expand.png");

    private void drawHut(GraphicsContext gc, Grid grid,
                         double x, double y, Building building, BuildingStyle style) {
        Image hutImage = PlayerTheme.of(building.getColor()).getHutImage();
        double width = grid.getHexRadiusX() / 2;
        double height = hutImage.getHeight() / (hutImage.getWidth() / width);
        gc.drawImage(hutImage, x - width / 2, y - height / 2, width, height);
    }

    private void drawTemple(GraphicsContext gc, Grid grid,
            double x, double y, Building building, BuildingStyle style) {
        y -= 10 * grid.getScale();
        Image templeImage = PlayerTheme.of(building.getColor()).getTempleImage();
        double width = 1.5 * grid.getHexRadiusX();
        double height = templeImage.getHeight() / (templeImage.getWidth() / width);
        gc.drawImage(templeImage, x - width / 2, y - height / 2, width, height);
    }

    private void drawTower(GraphicsContext gc, Grid grid,
            double x, double y, Building building, BuildingStyle style) {
        y -= 30 * grid.getScale();
        Image towerImage = PlayerTheme.of(building.getColor()).getTowerImage();
        double width = 4 * grid.getHexRadiusX() / 5;
        double height = towerImage.getHeight() / (towerImage.getWidth() / width);
        gc.drawImage(towerImage, x - width / 2, y - height / 2, width, height);
    }

    public void draw(GraphicsContext gc, Grid grid,
                     double x, double y, int level, Building building, BuildingStyle style) {
        if (style == BuildingStyle.EXPAND) {
            gc.drawImage(EXPAND_IMAGE, x-34, y-24);
            return;
        }

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
    }
}
