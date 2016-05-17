package ui;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import map.*;

import static ui.BuildingShapes.drawBuilding;
import static ui.HexShape.WEIRD_RATIO;

class IslandCanvas extends Canvas {

    static final Color BG_COLOR = Color.web("365373");

    static final Color BORDER_COLOR = Color.web("303030");
    static final Color BOTTOM_COLOR = Color.web("505050");

    private final Island island;
    private final boolean debug;
    private final HexShape hexShape;

    double ox;
    double oy;
    double scale;

    private Hex selectedHex;

    // Variable de selection de la tuile
    private Hex[] temporaryHex;
    private int selectedHexZone;
    final static int westGap = 3;
    final static int eastGap = 9;
    private boolean redrawTempTile;
    private final VolcanoTile temporaryTile;

    IslandCanvas(Island island, boolean debug) {
        super(0, 0);
        this.island = island;
        this.debug = debug;
        this.hexShape = new HexShape();

        this.ox = 0;
        this.ox = 0;
        this.scale = 1;

        // Variable de selection de la tuile
        this.selectedHex = null;
        this.selectedHexZone = -1;
        this.redrawTempTile = false;
        this.temporaryHex = new Hex[3];
        this.temporaryTile = new VolcanoTile(FieldType.JUNGLE, FieldType.JUNGLE);

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);

        setOnMouseClicked(this::mouseClicked);
        setOnMouseMoved(this::mouseMoved);
    }

    private void mouseMoved(MouseEvent event) {
        double x = event.getX() - (getWidth() / 2 - ox);
        double y = event.getY() - (getHeight() / 2 - oy);
        Hex newSelectedHex = pointToHex(x, y);

        if (!newSelectedHex.equals(selectedHex)) {
            selectedHex = newSelectedHex;
            //redraw();
        }
        int newselectedhexZone = pointToHexZone(x, y);
        if(newSelectedHex != selectedHex || newselectedhexZone != selectedHexZone) {
            temporaryHex[0] = newSelectedHex;
            temporaryHex[1] = newSelectedHex.getNeighbor(Neighbor.WEST);
            temporaryHex[2] =newSelectedHex.getNeighbor(Neighbor.EAST);;
            redrawTempTile = true;
            redraw();
        }
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

    private void mouseClicked(MouseEvent event) {
        double x = event.getX() - (getWidth() / 2 - ox);
        double y = event.getY() - (getHeight() / 2 - oy);
        pointToHexZone(x, y);
    }

    public int pointToHexZone (double x, double y) {
        double hexSizeX = HexShape.HEX_SIZE_X * scale;
        double hexSizeY = HexShape.HEX_SIZE_Y * scale;

        Hex hex = pointToHex(x, y);

        // Calcul du centre de la tuile
        double hexCenterX = hex.getDiag() * 2 * WEIRD_RATIO * hexSizeX + hex.getLine() * WEIRD_RATIO * hexSizeX;
        double hexCenterY = hex.getLine() * hexSizeY + hex.getLine() * hexSizeY / 2;

        double degree = Math.toDegrees(Math.atan((y - hexCenterY) / (x - hexCenterX)));

        // Calcul de la zone correspondante
        /*
          _______
         /\11| 0/\
        /10\ | / 1\
       /_9__\|/__2_\
       \ 8  /|\  3 /
        \7 / | \ 4/
         \/_6|5_\/

         */
        return (int) Math.floor(x > hexCenterX ? (degree/30) + westGap : (degree/30) + eastGap);
    }

    /*
    public VolcanoTile hexZoneToTile(Hex hex, int zone) {
        return new VolcanoTile()
    }
    */

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        double centerX = getWidth() / 2 - ox;
        double centerY = getHeight() / 2 - oy;
        double hexSizeX = HexShape.HEX_SIZE_X * scale;
        double hexSizeY = HexShape.HEX_SIZE_Y * scale;

        for (Hex hex : Hex.lineThenDiagOrdering().sortedCopy(island.getFields())) {
            int line = hex.getLine();
            int diag = hex.getDiag();
            double x = centerX + diag * 2 * WEIRD_RATIO * hexSizeX + line * WEIRD_RATIO * hexSizeX;
            double y = centerY + line * hexSizeY + line * hexSizeY / 2;
            Field field = island.getField(hex);
            boolean selected = selectedHex != null && hex.equals(selectedHex);

            hexShape.draw(gc, field, selected, x, y, hexSizeX, hexSizeY, scale);

            FieldBuilding building = field.getBuilding();
            if (building.getType() != BuildingType.NONE) {
                drawBuilding(gc, building, field.getLevel(), selected,
                        x, y - hexSizeY / 6, hexSizeX, hexSizeY);
            }
        }

        if (redrawTempTile) {/*
            for (Hex hex : temporaryHex) {
                int line = hex.getLine();
                int diag = hex.getDiag();
                double x = centerX + diag * 2 * WEIRD_RATIO * hexSizeX + line * WEIRD_RATIO * hexSizeX;
                double y = centerY + line * hexSizeY + line * hexSizeY / 2;
                Field field = island.getField(hex);
                boolean selected = selectedHex != null && hex.equals(selectedHex);

                hexShape.draw(gc, field, selected, x, y, hexSizeX, hexSizeY, scale);

            }
            redrawTempTile = false;
            */
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
