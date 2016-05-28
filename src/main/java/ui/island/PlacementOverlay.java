package ui.island;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Building;
import map.Island;
import map.Neighbor;
import ui.shape.BuildingShape;
import ui.shape.HexShape;
import ui.theme.BuildingStyle;
import ui.theme.HexStyle;

class PlacementOverlay extends Canvas {

    private final Grid grid;
    private final Island island;
    private final Placement placement;

    private final HexShape hexShape;
    private final BuildingShape buildingShape;

    PlacementOverlay(Island island, Grid grid, Placement placement) {
        super(0, 0);
        this.island = island;
        this.grid = grid;
        this.placement = placement;

        this.hexShape = new HexShape();
        this.buildingShape = new BuildingShape();

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);
    }

    private ImmutableList<HexToDraw> placedFreeInfos() {
        Neighbor leftNeighbor = Neighbor.leftOf(placement.tileOrientation);
        Neighbor rightNeighbor = Neighbor.rightOf(placement.tileOrientation);

        HexToDraw info1 = new HexToDraw(
                placement.mouseX,
                placement.mouseY,
                1,
                FieldType.VOLCANO,
                placement.tileOrientation,
                HexStyle.FLOATING,
                Building.none(),
                BuildingStyle.NORMAL);
        HexToDraw info2 = new HexToDraw(
                placement.mouseX + grid.neighborToXOffset(leftNeighbor),
                placement.mouseY + grid.neighborToYOffset(leftNeighbor),
                1,
                placement.tile.getLeft(),
                placement.tileOrientation.leftRotation(),
                HexStyle.FLOATING,
                Building.none(),
                BuildingStyle.NORMAL);
        HexToDraw info3 = new HexToDraw(
                placement.mouseX + grid.neighborToXOffset(rightNeighbor),
                placement.mouseY + grid.neighborToYOffset(rightNeighbor),
                1,
                placement.tile.getRight(),
                placement.tileOrientation.rightRotation(),
                HexStyle.FLOATING,
                Building.none(),
                BuildingStyle.NORMAL);

        return Ordering.natural().immutableSortedCopy(ImmutableList.of(info1, info2, info3));
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (!placement.valid) {
            if (placement.mode == Placement.Mode.BUILDING) {
                buildingShape.draw(gc, grid,
                        placement.mouseX + 5,
                        placement.mouseY,
                        island.getField(placement.hex).getLevel(),
                        Building.of(placement.buildingType, placement.buildingColor),
                        placement.valid ? BuildingStyle.HIGHLIGHTED : BuildingStyle.NORMAL);
            }
            else if (placement.mode == Placement.Mode.TILE) {
                for (HexToDraw info : placedFreeInfos()) {
                    hexShape.draw(gc, grid,
                            info.x, info.y, info.level, info.fieldType, info.orientation, info.hexStyle);
                }
            }
        }

        setTranslateX(0);
        setTranslateY(0);
    }
}
