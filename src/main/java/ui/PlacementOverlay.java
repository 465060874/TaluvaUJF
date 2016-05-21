package ui;

import com.google.common.collect.ImmutableList;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.Field;
import map.FieldBuilding;
import map.Island;
import map.Neighbor;

import static ui.BuildingShapes.drawBuilding;
import static ui.HexShape.WEIRD_RATIO;

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
        info1.isPlacement = info2.isPlacement = info3.isPlacement = true;
        info1.x = info2.x = info3.x = placement.mouseX;
        info1.y = info2.y = info3.y = placement.mouseY;
        info1.sizeX = info2.sizeX = info3.sizeX = HexShape.HEX_SIZE_X * grid.getScale();
        info1.sizeY = info2.sizeY = info3.sizeY = HexShape.HEX_SIZE_Y * grid.getScale();
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

        if (!placement.valid) {
            if (placement.mode == Placement.Mode.TILE) {
                for (HexShapeInfo info : placedFreeInfos()) {
                    hexShape.draw(gc, info);
                }
            }
            else if (placement.mode == Placement.Mode.BUILDING) {
                HexShapeInfo info = new HexShapeInfo();
                int line = placement.hex.getLine();
                int diag = placement.hex.getDiag();
                info.sizeX = HexShape.HEX_SIZE_X * grid.getScale();
                info.sizeY = HexShape.HEX_SIZE_Y * grid.getScale();
                info.x = placement.mouseX + 5;
                info.y = placement.mouseY;
                info.isPlacement = true;
                info.field = island.getField(placement.hex);
                info.scale = grid.getScale();
                FieldBuilding fieldBuilding = FieldBuilding.of(placement.buildingType, placement.buildingColor);
                drawBuilding(gc, fieldBuilding, Math.max(1, island.getField(placement.hex).getLevel()),
                        info.x,
                        info.y - info.sizeY / 6,
                        info.sizeX, info.sizeY);
            }
        }
    }

}
