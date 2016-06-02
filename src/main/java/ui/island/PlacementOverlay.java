package ui.island;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import map.*;
import theme.BuildingStyle;
import theme.HexStyle;
import ui.shape.BuildingShape;
import ui.shape.HexShape;

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

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (!placement.isValid()) {
            BuildingStyle buildingStyle;
            if (placement.mode == Placement.Mode.BUILDING) {
                if (placement.isValid()) {
                    buildingStyle = BuildingStyle.HIGHLIGHTED;
                }
                else {
                    final Hex hex = placement.getHex();
                    final Field field = island.getField(hex);
                    if (field.hasBuilding()
                            && (placement.getActivePlayerColor() == field.getBuilding().getColor())) {
                        buildingStyle = BuildingStyle.EXPAND;
                    } else if (field == Field.SEA){
                        buildingStyle = BuildingStyle.NORMAL;
                    } else {
                        buildingStyle = BuildingStyle.INVALID;
                    }
                }
                buildingShape.draw(gc, grid,
                        placement.mouseX + 5,
                        placement.mouseY,
                        1,
                        Building.of(placement.buildingType, placement.buildingColor),
                        buildingStyle);
            }
            else if (placement.mode == Placement.Mode.TILE) {
                for (HexToDraw info : placedHexes()) {
                    hexShape.draw(gc, grid,
                            info.x, info.y, info.level, info.fieldType, info.orientation, info.hexStyle);
                }
            }
        }

        setTranslateX(0);
        setTranslateY(0);
    }

    private ImmutableList<HexToDraw> placedHexes() {
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
}
