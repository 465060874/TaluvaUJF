package ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import data.BuildingType;
import data.FieldType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import map.*;

import java.util.ArrayList;
import java.util.List;

import static ui.BuildingShapes.drawBuilding;
import static ui.HexShape.WEIRD_RATIO;

class IslandCanvas extends Canvas {

    static final Color BG_COLOR = Color.web("5E81A2");

    static final Color BORDER_COLOR = Color.web("303030");
    static final Color BOTTOM_COLOR = Color.web("707070");

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
        info1.isPlacement = info2.isPlacement = info3.isPlacement = true;
        info1.sizeX = info2.sizeX = info3.sizeX = HexShape.HEX_SIZE_X * grid.getScale();
        info1.sizeY = info2.sizeY = info3.sizeY = HexShape.HEX_SIZE_Y * grid.getScale();

        int level = island.getField(placement.hex).getLevel() + 1;
        Hex hex1 = placement.hex;
        Hex hex2 = placement.hex.getLeftNeighbor(placement.tileOrientation);
        Hex hex3 = placement.hex.getRightNeighbor(placement.tileOrientation);
        info1.x = centerX + hex1.getDiag() * 2 * WEIRD_RATIO * info1.sizeX
                + hex1.getLine() * WEIRD_RATIO * info1.sizeX;
        info2.x = centerX + hex2.getDiag() * 2 * WEIRD_RATIO * info2.sizeX
                + hex2.getLine() * WEIRD_RATIO * info2.sizeX;
        info3.x = centerX + hex3.getDiag() * 2 * WEIRD_RATIO * info3.sizeX
                + hex3.getLine() * WEIRD_RATIO * info3.sizeX;
        info1.y = centerY + hex1.getLine() * info1.sizeY + hex1.getLine() * info1.sizeY / 2;
        info2.y = centerY + hex2.getLine() * info2.sizeY + hex2.getLine() * info2.sizeY / 2;
        info3.y = centerY + hex3.getLine() * info3.sizeY + hex3.getLine() * info3.sizeY / 2;
        info1.scale = info2.scale = info3.scale = grid.getScale();

        info1.field = Field.create(level, FieldType.VOLCANO, placement.tileOrientation);
        info2.field = Field.create(level, placement.tileFields.getLeft(), placement.tileOrientation);
        info3.field = Field.create(level, placement.tileFields.getRight(), placement.tileOrientation);

        return ImmutableList.of(info1, info2, info3);
    }

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        double hexSizeX = HexShape.HEX_SIZE_X * grid.getScale();
        double hexSizeY = HexShape.HEX_SIZE_Y * grid.getScale();
        double centerX = getWidth() / 2 - grid.getOx();
        double centerY = getHeight() / 2 - grid.getOy();

        List<HexShapeInfo> infos = new ArrayList<>();
        for (Hex hex : island.getFields()) {
            HexShapeInfo info = new HexShapeInfo();
            int line = hex.getLine();
            int diag = hex.getDiag();
            info.sizeX = hexSizeX;
            info.sizeY = hexSizeY;
            info.x = centerX + diag * 2 * WEIRD_RATIO * info.sizeX + line * WEIRD_RATIO * info.sizeX;
            info.y = centerY + line * info.sizeY + line * info.sizeY / 2;
            info.isPlacement = false;
            info.field = island.getField(hex);
            info.scale = grid.getScale();
            infos.add(info);
        }

        if (placement.valid && placement.mode == Placement.Mode.TILE) {
            infos.addAll(placedInfos(centerX, centerY));
        }

        // Affichage de la map
        for (HexShapeInfo info : Ordering.natural().sortedCopy(infos)) {
            hexShape.draw(gc, info);

            FieldBuilding building = info.field.getBuilding();
            if (building.getType() != BuildingType.NONE) {
                drawBuilding(gc, building, info.field.getLevel(),
                        info.x,
                        info.y - info.sizeY / 6,
                        info.sizeX, info.sizeY);
            }
        }

        if (placement.valid && placement.mode == Placement.Mode.BUILDING) {
            HexShapeInfo info = new HexShapeInfo();
            int line = placement.hex.getLine();
            int diag = placement.hex.getDiag();
            info.sizeX = hexSizeX;
            info.sizeY = hexSizeY;
            info.x = centerX + diag * 2 * WEIRD_RATIO * info.sizeX + line * WEIRD_RATIO * info.sizeX;
            info.y = centerY + line * info.sizeY + line * info.sizeY / 2;
            info.isPlacement = false;
            info.field = island.getField(placement.hex);
            info.scale = grid.getScale();
            infos.add(info);
            FieldBuilding fieldBuilding = FieldBuilding.of(placement.buildingType, placement.buildingColor);
            drawBuilding(gc, fieldBuilding, Math.max(1, island.getField(placement.hex).getLevel()),
                    info.x,
                    info.y - info.sizeY / 6,
                    info.sizeX, info.sizeY);
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

            minLine -= 1;
            minDiag -= 1;
            maxLine += 1;
            maxDiag += 1;
            for (int line = minLine; line <= maxLine; line++) {
                for (int diag = minDiag; diag <= maxDiag; diag++) {
                    double x = centerX + diag * 2 * WEIRD_RATIO * hexSizeX + line * WEIRD_RATIO * hexSizeX;
                    double y = centerY + line * hexSizeY + line * hexSizeY / 2;
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
}
