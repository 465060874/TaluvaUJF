package ui.island;

import com.google.common.collect.ImmutableList;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Field;
import map.FieldBuilding;
import map.Island;
import map.Neighbor;
import ui.shape.HexShape;
import ui.shape.HexShapeInfo;
import ui.theme.PlacementState;

import static ui.island.Grid.HEX_SIZE_X;
import static ui.island.Grid.HEX_SIZE_Y;
import static ui.island.Grid.WEIRD_RATIO;
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
        info1.placementState = info2.placementState = info3.placementState = PlacementState.INVALID;
        info1.x = info2.x = info3.x = placement.mouseX;
        info1.y = info2.y = info3.y = placement.mouseY;
        info1.sizeX = info2.sizeX = info3.sizeX = HEX_SIZE_X * grid.getScale();
        info1.sizeY = info2.sizeY = info3.sizeY = HEX_SIZE_Y * grid.getScale();
        info1.scale = info2.scale = info3.scale = grid.getScale();

        Neighbor leftNeighbor = Neighbor.leftOf(placement.tileOrientation);
        info2.x += leftNeighbor.getDiagOffset() * 2 * WEIRD_RATIO * info2.sizeX
                + leftNeighbor.getLineOffset() * WEIRD_RATIO * info2.sizeX;
        info2.y += leftNeighbor.getLineOffset() * info2.sizeY + leftNeighbor.getLineOffset() * info2.sizeY / 2;
        Neighbor rightNeighbor = Neighbor.rightOf(placement.tileOrientation);
        info3.x += rightNeighbor.getDiagOffset() * 2 * WEIRD_RATIO * info3.sizeX
                + rightNeighbor.getLineOffset() * WEIRD_RATIO * info3.sizeX;
        info3.y += rightNeighbor.getLineOffset() * info3.sizeY + rightNeighbor.getLineOffset() * info3.sizeY / 2;

        info1.field = Field.create(1, FieldType.VOLCANO, placement.tileOrientation);
        info2.field = Field.create(1, placement.tileFields.getLeft(), placement.tileOrientation.leftRotation());
        info3.field = Field.create(1, placement.tileFields.getRight(), placement.tileOrientation.rightRotation());

        return ImmutableList.of(info1, info2, info3);
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (placement.mode == Placement.Mode.BUILDING) {
            HexShapeInfo info = new HexShapeInfo();

            info.sizeX = HEX_SIZE_X * grid.getScale();
            info.sizeY = HEX_SIZE_Y * grid.getScale();
            if (placement.valid) {
                double centerX = getWidth() / 2 - grid.getOx();
                double centerY = getHeight() / 2 - grid.getOy();
                int line = placement.hex.getLine();
                int diag = placement.hex.getDiag();
                info.x = centerX + diag * 2 * WEIRD_RATIO * info.sizeX + line * WEIRD_RATIO * info.sizeX;
                info.y = centerY + line * info.sizeY + line * info.sizeY / 2;
            }
            else {
                info.x = placement.mouseX + 5;
                info.y = placement.mouseY;
            }

            info.placementState = PlacementState.INVALID;
            info.field = island.getField(placement.hex);
            info.scale = grid.getScale();
            FieldBuilding fieldBuilding = FieldBuilding.of(placement.buildingType, placement.buildingColor);
            drawBuilding(gc, fieldBuilding, Math.max(1, island.getField(placement.hex).getLevel()),
                    placement.valid ? PlacementState.VALID : PlacementState.INVALID,
                    grid.scale,
                    info.x, info.y - info.sizeY / 6,
                    info.sizeX, info.sizeY);
        }
        else if (placement.mode == Placement.Mode.TILE && !placement.valid) {
            for (HexShapeInfo info : placedFreeInfos()) {
                hexShape.draw(gc, info);
            }
        }

        setTranslateX(0);
        setTranslateY(0);
    }

}
