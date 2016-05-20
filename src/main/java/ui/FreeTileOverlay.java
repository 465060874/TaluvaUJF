package ui;

import com.google.common.collect.ImmutableList;
import data.FieldType;
import data.VolcanoTile;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import map.*;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import static ui.HexShape.WEIRD_RATIO;

class FreeTileOverlay extends Canvas {

    private final Grid grid;
    private final Island island;
    private final HexShape hexShape;
    private final boolean placedTileOrientationAuto;


    // Variable de placement de la tuile
    private Hex placedHex;
    private VolcanoTile placedTile;
    private Orientation placedTileRotation;
    private Orientation placedTileOrientation;
    private double freeTileX;
    private double freeTileY;

    FreeTileOverlay(Island island, Grid grid) {
        super(0, 0);
        this.grid = grid;
        this.island = island;
        this.hexShape = new HexShape();

        // Variable de selection de la tuile
        this.placedHex = null;
        this.placedTileRotation = Orientation.NORTH;
        this.placedTile = new VolcanoTile(FieldType.CLEARING, FieldType.SAND);
        this.placedTileOrientation = Orientation.NORTH;
        this.placedTileOrientationAuto = false;
        this.freeTileX = 0.0;
        this.freeTileY = 0.0;

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);

        setOnMouseClicked(this::mouseClicked);
    }

    void mouseMovedAction(MouseEvent event, double width, double height) {
        Hex newPlacedHex = grid.getHex(event, width, height);

        boolean isRobinson = StreamSupport.stream(newPlacedHex.getNeighborhood().spliterator(), false)
                .allMatch(h -> island.getField(h) == Field.SEA);

        if (isRobinson) {
            System.out.println("Visible");
            freeTileX = event.getX();
            freeTileY = event.getY();
            placedTileOrientation = getCLosestNeighborOrientation(newPlacedHex);
            placedHex = null;
            redraw();
        }
    }


    private void mouseClicked(MouseEvent event) {
        if (MouseButton.SECONDARY.equals(event.getButton())) {
            placedTileRotation = placedTileRotation.clockWise().clockWise();
            redraw();
        }
    }

    private Orientation getCLosestNeighborOrientation(Hex newSelectedHex) {

        if (!placedTileOrientationAuto) return Orientation.NORTH;

        final Orientation[] orientations = Orientation.values();
        Hex[] orientationIndex = new Hex[orientations.length];
        Arrays.fill(orientationIndex, Hex.at(newSelectedHex.getLine(), newSelectedHex.getDiag()));
        int j = 0;
        int i = 0;
        while(j < 6*10) {
            while( i < orientationIndex.length) {
                orientationIndex[i] = orientations[i].getFrontHex(orientationIndex[i]);
                if (island.getField(orientationIndex[i]) != Field.SEA) {
                    return orientations[i];
                }
                i++;
            }
            if (i == orientationIndex.length) i = 0;
            j++;
        }

        return Orientation.NORTH;
    }

    private ImmutableList<HexShapeInfo> placedFreeInfos() {
        HexShapeInfo info1 = new HexShapeInfo();
        HexShapeInfo info2 = new HexShapeInfo();
        HexShapeInfo info3 = new HexShapeInfo();
        info1.isPlacement = info2.isPlacement = info3.isPlacement = true;
        info1.x = info2.x = info3.x = freeTileX;
        info1.y = info2.y = info3.y = freeTileY;
        info1.sizeX = info2.sizeX = info3.sizeX = HexShape.HEX_SIZE_X * grid.getScale();
        info1.sizeY = info2.sizeY = info3.sizeY = HexShape.HEX_SIZE_Y * grid.getScale();
        info1.scale = info2.scale = info3.scale = grid.getScale();

        Neighbor leftNeighbor = Neighbor.leftOf(placedTileOrientation);
        info2.x += leftNeighbor.getDiagOffset() * 2 * WEIRD_RATIO * info2.sizeX
                + leftNeighbor.getLineOffset() * WEIRD_RATIO * info2.sizeX;
        info2.y += leftNeighbor.getLineOffset() * info2.sizeY + leftNeighbor.getLineOffset() * info2.sizeY / 2;
        Neighbor rightNeighbor = Neighbor.rightOf(placedTileOrientation);
        info3.x += rightNeighbor.getDiagOffset() * 2 * WEIRD_RATIO * info3.sizeX
                + rightNeighbor.getLineOffset() * WEIRD_RATIO * info3.sizeX;
        info3.y += rightNeighbor.getLineOffset() * info3.sizeY + rightNeighbor.getLineOffset() * info3.sizeY / 2;

        if (placedTileRotation == Orientation.NORTH) {
            info1.field = Field.create(1, FieldType.VOLCANO, placedTileOrientation);
            info2.field = Field.create(1, placedTile.getLeft(), placedTileOrientation.leftRotation());
            info3.field = Field.create(1, placedTile.getRight(), placedTileOrientation.rightRotation());
        } else if (placedTileRotation == Orientation.SOUTH_EAST) {
            info1.field = Field.create(1, placedTile.getLeft(), placedTileOrientation);
            info2.field = Field.create(1, placedTile.getRight(), placedTileOrientation.leftRotation());
            info3.field = Field.create(1, FieldType.VOLCANO, placedTileOrientation.rightRotation());
        } else {
            info1.field = Field.create(1, placedTile.getRight(), placedTileOrientation);
            info2.field = Field.create(1, FieldType.VOLCANO, placedTileOrientation.leftRotation());
            info3.field = Field.create(1, placedTile.getLeft(), placedTileOrientation.rightRotation());
        }

        return ImmutableList.of(info1, info2, info3);
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (placedHex == null) {
            for (HexShapeInfo info : placedFreeInfos()) {
                hexShape.draw(gc, info);
            }
        }
    }

}
