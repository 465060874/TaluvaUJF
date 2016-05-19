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

public class FreeTileCanvas extends Canvas {

    private static final int westGap = 3;
    private static final int eastGap = 9;

    private final Island island;
    private final HexShape hexShape;
    private final boolean placedTileOrientationAuto;

    double ox;
    double oy;
    double scale;


    // Variable de placement de la tuile
    private Hex placedHex;
    private VolcanoTile placedTile;
    private Orientation placedTileRotation;
    private Orientation placedTileOrientation;
    private double freeTileX;
    private double freeTileY;
    private boolean active;

    FreeTileCanvas(Island island, boolean debug) {
        super(0, 0);
        this.island = island;
        //TODO Reimplement debug view is this pane
        this.hexShape = new HexShape();

        this.ox = 0;
        this.oy = 0;
        this.scale = 1;

        // Variable de selection de la tuile
        this.placedHex = null;
        this.placedTileRotation = Orientation.NORTH;
        this.placedTile = new VolcanoTile(FieldType.CLEARING, FieldType.SAND);
        this.placedTileOrientation = Orientation.NORTH;
        this.placedTileOrientationAuto = true;
        this.freeTileX = 0.0;
        this.freeTileY = 0.0;
        this.active = true;

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);

        setOnMouseMoved(this::mouseMoved);
        setOnMouseClicked(this::mouseClicked);
    }

    private void mouseClicked(MouseEvent event) {
        if (MouseButton.SECONDARY.equals(event.getButton())) {
            placedTileRotation = placedTileRotation.clockWise().clockWise();
            redraw();
        }
    }

    private void mouseMoved(MouseEvent event) {
        double x = event.getX() - (getWidth() / 2 - ox);
        double y = event.getY() - (getHeight() / 2 - oy);
        Hex newPlacedHex = pointToHex(x, y);

        boolean isRobinson = StreamSupport.stream(newPlacedHex.getNeighborhood().spliterator(), false)
                .allMatch(h -> island.getField(h) == Field.SEA);

        if (isRobinson) {
            System.out.println("Visible");
            freeTileX = event.getX();
            freeTileY = event.getY();
            placedTileOrientation = getCLosestNeighborOrientation(newPlacedHex);
            placedHex = null;
            active = true;
            redraw();
        } else {
            System.out.println("Not Visible");
            this.setVisible(false);
            active = false;
            //redraw();
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

    private Hex pointToHex(double x, double y) {
        double hexWidth = HexShape.HEX_SIZE_X * scale * WEIRD_RATIO;
        double hexHeight = HexShape.HEX_SIZE_Y * scale;

        x = x / (hexWidth * 2);
        double t1 = (y + hexHeight) / hexHeight;
        double t2 = Math.floor(x + t1);
        double line = Math.floor((Math.floor(t1 - x) + t2) / 3);
        double diag = Math.floor((Math.floor(2 * x + 1) + t2) / 3) - line;

        return Hex.at((int) line, (int) diag);
    }

    private ImmutableList<HexShapeInfo> placedFreeInfos() {
        HexShapeInfo info1 = new HexShapeInfo();
        HexShapeInfo info2 = new HexShapeInfo();
        HexShapeInfo info3 = new HexShapeInfo();
        info1.isPlacement = info2.isPlacement = info3.isPlacement = true;
        info1.x = info2.x = info3.x = freeTileX;
        info1.y = info2.y = info3.y = freeTileY;
        info1.sizeX = info2.sizeX = info3.sizeX = HexShape.HEX_SIZE_X * scale;
        info1.sizeY = info2.sizeY = info3.sizeY = HexShape.HEX_SIZE_Y * scale;
        info1.scale = info2.scale = info3.scale = scale;

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

        if (!isVisible()) { return; }

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (placedHex == null) {
            for (HexShapeInfo info : placedFreeInfos()) {
                hexShape.draw(gc, info);
            }
        }

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
    }

    public boolean isActive() {
        return active;
    }
}
