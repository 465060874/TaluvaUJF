package ui.island;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import data.BuildingType;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Building;
import map.Island;
import map.Neighbor;
import ui.shape.HexShape;
import ui.shape.HexShapeInfo;
import ui.theme.PlacementState;

import static ui.shape.BuildingShapes.drawBuilding;

class PlacementOverlay extends Canvas {

    private final Grid grid;
    private final Island island;
    private final HexShape hexShape;
    private final Placement placement;

    PlacementOverlay(Island island, Grid grid, Placement placement) {
        super(0, 0);
        this.island = island;
        this.grid = grid;
        this.placement = placement;
        this.hexShape = new HexShape();

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);
    }

    private ImmutableList<HexShapeInfo> placedFreeInfos() {
        HexShapeInfo info1 = new HexShapeInfo();
        HexShapeInfo info2 = new HexShapeInfo();
        HexShapeInfo info3 = new HexShapeInfo();

        info1.placementState = info2.placementState = info3.placementState = PlacementState.FLOATING;
        info1.x = info2.x = info3.x = placement.mouseX;
        info1.y = info2.y = info3.y = placement.mouseY;

        info1.level = 1;
        info1.fieldType = FieldType.VOLCANO;
        info1.orientation = placement.tileOrientation;
        info1.building = Building.of(BuildingType.NONE, null);

        Neighbor leftNeighbor = Neighbor.leftOf(placement.tileOrientation);
        info2.x += grid.neighborToXOffset(leftNeighbor);
        info2.y += grid.neighborToYOffset(leftNeighbor);
        info2.level = 1;
        info2.fieldType = placement.tile.getLeft();
        info2.orientation = placement.tileOrientation.leftRotation();
        info2.building = Building.of(BuildingType.NONE, null);

        Neighbor rightNeighbor = Neighbor.rightOf(placement.tileOrientation);
        info3.x += grid.neighborToXOffset(rightNeighbor);
        info3.y += grid.neighborToYOffset(rightNeighbor);
        info3.level = 1;
        info3.fieldType = placement.tile.getRight();
        info3.orientation = placement.tileOrientation.rightRotation();
        info3.building = Building.of(BuildingType.NONE, null);

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
                HexShapeInfo info = new HexShapeInfo();

                if (placement.valid) {
                    info.x = grid.hexToX(placement.hex, getWidth());
                    info.y = grid.hexToY(placement.hex, getHeight());
                }
                else {
                    info.x = placement.mouseX + 5;
                    info.y = placement.mouseY;
                }

                info.placementState = placement.valid ? PlacementState.VALID : PlacementState.FLOATING;
                info.level = island.getField(placement.hex).getLevel();
                info.building = Building.of(placement.buildingType, placement.buildingColor);
                drawBuilding(gc, grid, info);
            }
            else if (placement.mode == Placement.Mode.TILE) {
                for (HexShapeInfo info : placedFreeInfos()) {
                    hexShape.draw(gc, grid, info);
                }
            }
        }

        setTranslateX(0);
        setTranslateY(0);
    }

}
