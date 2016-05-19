package ui;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import map.Hex;
import map.Island;
import map.Orientation;

import static ui.HexShape.WEIRD_RATIO;

public class FreeTileCanvas extends Canvas {

    static final Color BG_COLOR = Color.web("5E81A2");

    static final Color BORDER_COLOR = Color.web("303030");
    static final Color BOTTOM_COLOR = Color.web("707070");

    private static final int westGap = 3;
    private static final int eastGap = 9;

    private final Island island;
    private final boolean debug;
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
    private boolean isBuildingMode;

    // Variable de selection du Batiment
    private BuildingType selectedBuildingType;

    public FreeTileCanvas(Island island, Boolean debug) {
        super(0, 0);
        this.island = island;
        this.debug = debug;
        this.hexShape = new HexShape();

        this.ox = 0;
        this.ox = 0;
        this.scale = 1;

        // Variable de selection de la tuile
        this.placedHex = null;
        this.placedTileRotation = Orientation.NORTH;
        this.placedTile = new VolcanoTile(FieldType.CLEARING, FieldType.SAND);
        this.placedTileOrientation = Orientation.NORTH;
        this.placedTileOrientationAuto = false;
        this.freeTileX = 0.0;
        this.freeTileY = 0.0;

        setOnMouseMoved(this::mouseMoved);
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

    private void mouseMoved(MouseEvent event) {
        double x = event.getX() - (getWidth() / 2 - ox);
        double y = event.getY() - (getHeight() / 2 - oy);
        Hex newPlacedHex = pointToHex(x, y);
        if (newPlacedHex != placedHex) {
            placedHex = newPlacedHex;
            redraw();
        }
    }

    void redraw() {
        double hexSizeX = HexShape.HEX_SIZE_X * scale;
        double hexSizeY = HexShape.HEX_SIZE_Y * scale;
        double centerX = getWidth() / 2 - ox;
        double centerY = getHeight() / 2 - oy;

        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        gc.fillText("Coco", centerX, centerY);


        /*
        double hexSizeX = HexShape.HEX_SIZE_X * scale;
        double hexSizeY = HexShape.HEX_SIZE_Y * scale;
        double centerX = getWidth() / 2 - ox;
        double centerY = getHeight() / 2 - oy;

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
            info.scale = scale;
            infos.add(info);
        }

        if (!isBuildingMode && placedHex != null) {
            infos.addAll(placedInfos(centerX, centerY));
        }

        if (!isBuildingMode && placedHex == null) {
            for (HexShapeInfo info : placedFreeInfos()) {
                hexShape.draw(gc, info);
            }
        }

        if (placedHex != null && isBuildingMode && island.getField(placedHex) != Field.SEA) {
            HexShapeInfo info = new HexShapeInfo();
            int line = placedHex.getLine();
            int diag = placedHex.getDiag();
            info.sizeX = hexSizeX;
            info.sizeY = hexSizeY;
            info.x = centerX + diag * 2 * WEIRD_RATIO * info.sizeX + line * WEIRD_RATIO * info.sizeX;
            info.y = centerY + line * info.sizeY + line * info.sizeY / 2;
            info.isPlacement = false;
            info.field = island.getField(placedHex);
            info.scale = scale;
            infos.add(info);
            drawBuilding(gc, FieldBuilding.of(selectedBuildingType, PlayerColor.RED), Math.max(1, island.getField(placedHex).getLevel()),
                    info.x,
                    info.y - info.sizeY / 6,
                    info.sizeX, info.sizeY);

        }

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
        */
    }
}
