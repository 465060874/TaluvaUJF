package ui.island;

import com.google.common.collect.Ordering;
import data.BuildingType;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import map.*;
import theme.BuildingStyle;
import theme.HexStyle;
import theme.IslandTheme;
import ui.shape.BuildingShape;
import ui.shape.HexShape;

import java.util.ArrayList;
import java.util.List;

import static data.FieldType.VOLCANO;

class IslandCanvas extends Canvas {

    private final Island island;
    private final Grid grid;
    private final Placement placement;
    private final boolean debug;

    private final HexShape hexShape;
    private final BuildingShape buildingShape;
    private boolean forbiddenPlacementVisible;
    private boolean forbiddenBuildingsVisible;

    IslandCanvas(Island island, Grid grid, Placement placement, boolean debug) {
        super(0, 0);
        this.island = island;
        this.grid = grid;
        this.placement = placement;
        this.debug = debug;
        this.forbiddenPlacementVisible = false;
        this.forbiddenBuildingsVisible = false;

        this.hexShape = new HexShape();
        this.buildingShape = new BuildingShape();

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(IslandTheme.getCurrent().getBackgroundPaint());
        if (gc.getFill().isOpaque()) {
            gc.clearRect(0, 0, getWidth(), getHeight());
        }
        gc.fillRect(0, 0, getWidth(), getHeight());

        List<HexToDraw> hexesToDraw = new ArrayList<>();
        islandHexes(hexesToDraw);

        if (placement.isValid() && placement.mode == Placement.Mode.TILE) {
            placedHexes(hexesToDraw);
        }

        // Affichage de la map
        for (HexToDraw hexToDraw : Ordering.natural().sortedCopy(hexesToDraw)) {
            hexShape.draw(gc, grid,
                    hexToDraw.x, hexToDraw.y, hexToDraw.level,
                    hexToDraw.fieldType, hexToDraw.orientation, hexToDraw.hexStyle);

            if (hexToDraw.building.getType() != BuildingType.NONE) {
                buildingShape.draw(gc, grid,
                        hexToDraw.x, hexToDraw.y, hexToDraw.level,
                        hexToDraw.building, hexToDraw.buildingStyle);
            }
        }

        if (debug) {
            drawCoordinates(gc);
        }

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
    }

    private void islandHexes(List<HexToDraw> hexesToDraw) {
        for (Hex hex : island.getFields()) {
            double x = grid.hexToX(hex, getWidth());
            double y = grid.hexToY(hex, getHeight());
            if (isOutside(x, y)) {
                continue;
            }

            Field field = island.getField(hex);
            int level = field.getLevel();
            FieldType fieldType = field.getType();
            Orientation orientation = field.getOrientation();
            Building building;
            HexStyle hexStyle;
            if (placement.mode == Placement.Mode.TILE) {
                building = field.getBuilding();
                boolean isLastPlaced = placement.isLastPlaced(hex);
                if (forbiddenPlacementVisible) {
                    hexStyle = placement.validHexes.contains(hex)
                            ? isLastPlaced ? HexStyle.LASTPLAYED : HexStyle.NORMAL
                            : isLastPlaced ? HexStyle.FADEDLASTPLAYED : HexStyle.FADED;
                } else {
                    if (isLastPlaced) {
                        hexStyle = HexStyle.LASTPLAYED;
                    } else {
                        hexStyle = HexStyle.NORMAL;
                    }
                }
            }
            else if (placement.mode == Placement.Mode.EXPAND_VILLAGE) {
                if (placement.expansionHexes.contains(hex)) {
                    building = Building.of(BuildingType.HUT, placement.expansionVillage.getColor());
                    hexStyle = HexStyle.HIGHLIGHTED;
                }
                else if (placement.expansionVillage.getHexes().contains(hex)
                        || placement.expansionVillage.getExpandableHexes().containsValue(hex)) {
                    building = field.getBuilding();
                    hexStyle = HexStyle.NORMAL;
                }
                else {
                    building = field.getBuilding();
                    hexStyle = HexStyle.TRULYFADED;
                }
            }
            else if (placement.mode == Placement.Mode.BUILDING) {
                if (placement.isValid() && placement.hex.equals(hex)) {
                    building = Building.of(placement.buildingType, placement.buildingColor);
                    hexStyle = HexStyle.HIGHLIGHTED;
                }
                else if (island.getField(placement.hex).hasBuilding()) {
                    if (island.getVillage(placement.hex).getHexes().contains(hex)
                            || island.getVillage(placement.hex).getExpandableHexes().containsValue(hex)) {
                        building = field.getBuilding();
                        hexStyle = HexStyle.NORMAL;
                    }
                    else {
                        building = field.getBuilding();
                        hexStyle = HexStyle.TRULYFADED;
                    }
                }
                else if (placement.validHexes.contains(hex)) {
                    building = field.getBuilding();
                    hexStyle = HexStyle.NORMAL;
                }
                else {
                    building = field.getBuilding();
                    if (forbiddenBuildingsVisible) {
                        hexStyle = HexStyle.FADED;
                    } else {
                        hexStyle = HexStyle.NORMAL;
                    }
                }
            }
            else {
                building = field.getBuilding();
                hexStyle = HexStyle.NORMAL;
            }

            BuildingStyle buildingStyle;
            if (placement.isLastPlacedBuildings(hex)) {
                buildingStyle = BuildingStyle.LASTPLACED;
            } else {
                buildingStyle = BuildingStyle.NORMAL;
            }

            hexesToDraw.add(new HexToDraw(x, y, level,
                    fieldType, orientation, hexStyle,
                    building, buildingStyle));
        }
    }

    private void placedHexes(List<HexToDraw> hexesToDraw) {
        Hex hexTop = placement.hex;
        Hex hexLeft = placement.hex.getLeftNeighbor(placement.tileOrientation);
        Hex hexRight = placement.hex.getRightNeighbor(placement.tileOrientation);

        int level = island.getField(placement.hex).getLevel() + 1;
        HexToDraw info1 = new HexToDraw(
                grid.hexToX(hexTop, getWidth()),
                grid.hexToY(hexTop, getHeight()),
                level,
                VOLCANO,
                placement.tileOrientation,
                HexStyle.HIGHLIGHTED,
                Building.none(),
                BuildingStyle.NORMAL);
        HexToDraw info2 = new HexToDraw(
                grid.hexToX(hexLeft, getWidth()),
                grid.hexToY(hexLeft, getHeight()),
                level,
                placement.tile.getLeft(),
                placement.tileOrientation.leftRotation(),
                HexStyle.HIGHLIGHTED,
                Building.none(),
                BuildingStyle.NORMAL);
        HexToDraw info3 = new HexToDraw(
                grid.hexToX(hexRight, getWidth()),
                grid.hexToY(hexRight, getHeight()),
                level,
                placement.tile.getRight(),
                placement.tileOrientation.rightRotation(),
                HexStyle.HIGHLIGHTED,
                Building.none(),
                BuildingStyle.NORMAL);

        if (!isOutside(info1.x, info1.y)) {
            hexesToDraw.add(info1);
        }
        if (!isOutside(info2.x, info2.y)) {
            hexesToDraw.add(info2);
        }
        if (!isOutside(info3.x, info3.y)) {
            hexesToDraw.add(info3);
        }
    }

    private boolean isOutside(double x, double y) {
        double minX = x - grid.getHexRadiusX();
        double minY = y - grid.getHexRadiusY();
        double maxX = x - grid.getHexRadiusX();
        double maxY = y - grid.getHexRadiusY();
        return maxX < 0 || maxY < 0 || minX > getWidth() || minY > getHeight();
    }

    private void drawCoordinates(GraphicsContext gc) {
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

        minLine -= 3;
        minDiag -= 3;
        maxLine += 3;
        maxDiag += 3;
        for (int line = minLine; line <= maxLine; line++) {
            for (int diag = minDiag; diag <= maxDiag; diag++) {
                Hex hex = Hex.at(line, diag);
                double x = grid.hexToX(hex, getWidth());
                double y = grid.hexToY(hex, getHeight());
                String hexStr = line + "," + diag;
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setFill(Color.BLACK);
                gc.fillText(hexStr, x, y);
            }
        }
    }

    boolean hasForbiddenPlacementVisible() {
        return forbiddenPlacementVisible;
    }

    void setForbiddenPlacementVisible() {
        forbiddenPlacementVisible = true;
    }

    void setForbiddenPlacementInvisible() {
        forbiddenPlacementVisible = false;
    }

    public boolean hasForbiddenBuildingsVisible() {
        return forbiddenBuildingsVisible;
    }

    public void setForbiddenBuildingsInvisible() {
        forbiddenBuildingsVisible = false;
    }

    public void setForbiddenBuildingsVisible() {
        forbiddenBuildingsVisible = true;
    }
}
