package ui.island;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import data.BuildingType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import map.Field;
import map.Building;
import map.Hex;
import map.Island;
import ui.shape.HexShape;
import ui.shape.HexShapeInfo;
import ui.theme.PlacementState;

import java.util.ArrayList;
import java.util.List;

import static data.FieldType.VOLCANO;
import static ui.shape.BuildingShapes.drawBuilding;

class IslandCanvas extends Canvas {

    private final Island island;
    private final boolean debug;
    private final HexShape hexShape;
    private final Grid grid;
    private final Placement placement;

    IslandCanvas(Island island, Grid grid, Placement placement, boolean debug) {
        super(0, 0);
        this.island = island;
        this.grid = grid;
        this.placement = placement;
        this.debug = debug;
        this.hexShape = new HexShape();

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);
    }

    private ImmutableList<HexShapeInfo> placedInfos(double centerX, double centerY) {
        HexShapeInfo info1 = new HexShapeInfo();
        HexShapeInfo info2 = new HexShapeInfo();
        HexShapeInfo info3 = new HexShapeInfo();

        info1.placementState = info2.placementState = info3.placementState = PlacementState.VALID;

        int level = island.getField(placement.hex).getLevel() + 1;

        Hex hex1 = placement.hex;
        info1.x = grid.hexToX(hex1, getWidth());
        info1.y = grid.hexToY(hex1, getHeight());
        info1.level = level;
        info1.fieldType = VOLCANO;
        info1.orientation = placement.tileOrientation;
        info1.building = Building.of(BuildingType.NONE, null);

        Hex hex2 = placement.hex.getLeftNeighbor(placement.tileOrientation);
        info2.x = grid.hexToX(hex2, getWidth());
        info2.y = grid.hexToY(hex2, getHeight());
        info2.level = level;
        info2.fieldType = placement.tile.getLeft();
        info2.orientation = placement.tileOrientation.leftRotation();
        info2.building = Building.of(BuildingType.NONE, null);

        Hex hex3 = placement.hex.getRightNeighbor(placement.tileOrientation);
        info3.x = grid.hexToX(hex3, getWidth());
        info3.y = grid.hexToY(hex3, getHeight());
        info3.level = level;
        info3.fieldType = placement.tile.getRight();
        info3.orientation = placement.tileOrientation.rightRotation();
        info3.building = Building.of(BuildingType.NONE, null);

        return ImmutableList.of(info1, info2, info3);
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        double centerX = getWidth() / 2 - grid.getOx();
        double centerY = getHeight() / 2 - grid.getOy();

        List<HexShapeInfo> infos = new ArrayList<>();
        for (Hex hex : island.getFields()) {
            HexShapeInfo info = new HexShapeInfo();
            info.x = grid.hexToX(hex, getWidth());
            info.y = grid.hexToY(hex, getHeight());
            info.placementState = PlacementState.NONE;
            Field field = island.getField(hex);
            info.level = field.getLevel();
            info.fieldType = field.getType();
            info.orientation = field.getOrientation();
            if (placement.mode == Placement.Mode.EXPAND_VILLAGE) {
                if (placement.expansionHexes.contains(hex)) {
                    info.building = Building.of(BuildingType.HUT, placement.expansionVillage.getColor());
                    info.placementState = PlacementState.VALID;
                }
                else if (placement.expansionVillage.getHexes().contains(hex)
                        || placement.expansionVillage.getExpandableHexes().containsValue(hex)) {
                    info.building = field.getBuilding();
                    info.placementState = PlacementState.NONE;
                }
                else {
                    info.building = field.getBuilding();
                    info.placementState = PlacementState.INVALID;
                }
            }
            else if (placement.valid && placement.mode == Placement.Mode.BUILDING) {
                if (placement.hex.equals(hex)) {
                    info.placementState = PlacementState.VALID;
                    info.building = Building.of(placement.buildingType, placement.buildingColor);
                }
                else {
                    info.building = field.getBuilding();
                    info.placementState = PlacementState.NONE;
                }
            }
            else {
                info.building = field.getBuilding();
                info.placementState = PlacementState.NONE;
            }

            if (!isOutside(info)) {
                infos.add(info);
            }
        }

        if (placement.valid && placement.mode == Placement.Mode.TILE) {
            placedInfos(centerX, centerY).stream()
                    .filter(info -> !isOutside(info))
                    .forEach(infos::add);
        }

        // Affichage de la map
        for (HexShapeInfo info : Ordering.natural().sortedCopy(infos)) {
            hexShape.draw(gc, grid, info);

            Building building = info.building;
            if (building.getType() != BuildingType.NONE) {
                //info.y -= grid.getHexRadiusY();
                drawBuilding(gc, grid, info);
            }
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

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
    }

    private boolean isOutside(HexShapeInfo info) {
        double minX = info.x - grid.getHexRadiusX();
        double minY = info.y - grid.getHexRadiusY();
        double maxX = info.x - grid.getHexRadiusX();
        double maxY = info.y - grid.getHexRadiusY();
        return maxX < 0 || maxY < 0 || minX > getWidth() || minY > getHeight();
    }
}
