package ui;

import data.BuildingType;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import map.Field;
import map.FieldBuilding;
import map.Hex;
import map.Island;

import static ui.BuildingShapes.*;
import static ui.HexShape.WEIRD_RATIO;

class IslandCanvas extends Canvas {

    static final Color BG_COLOR = Color.web("365373");

    static final Color BORDER_COLOR = Color.web("352535");
    static final Color BOTTOM_COLOR = Color.web("505050");

    private final Island island;
    private final boolean debug;
    private final HexShape hexShape;

    double ox;
    double oy;
    double scale;

    private Hex selectedHex;

    IslandCanvas(Island island, boolean debug) {
        super(0, 0);
        this.island = island;
        this.debug = debug;
        this.hexShape = new HexShape();

        this.ox = 0;
        this.ox = 0;
        this.scale = 1;

        this.selectedHex = null;

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);

        setOnMouseMoved(this::mouseMoved);
    }

    private void mouseMoved(MouseEvent event) {
        double x = event.getX() - (getWidth() / 2 - ox);
        double y = event.getY() - (getHeight() / 2 - oy);
        selectedHex = pointToHex(x, y);
        redraw();
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

    private void resize(Observable event) {
        redraw();
    }

    void redraw() {
        GraphicsContext gc = getGraphicsContext2D();

        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());

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

            y -= hexSizeY / 6;
            FieldBuilding building = field.getBuilding();
            if (building.getType() != BuildingType.NONE) {
                switch (building.getType()) {
                    case HUT:
                        if (building.getCount() == 1) {
                            drawHut(gc, building, selected, x, y, hexSizeX, hexSizeY);
                        }
                        else if (building.getCount() == 2) {
                            drawHut(gc, building, selected, x - hexSizeX / 3, y, hexSizeX, hexSizeY);
                            drawHut(gc, building, selected, x + hexSizeX / 3, y, hexSizeX, hexSizeY);
                        }
                        else {
                            // TODO: More than 3
                            drawHut(gc, building, selected, x - hexSizeX / 3, y - hexSizeY / 3, hexSizeX, hexSizeY);
                            drawHut(gc, building, selected, x + hexSizeX / 3, y - hexSizeY / 3, hexSizeX, hexSizeY);
                            drawHut(gc, building, selected, x, y + hexSizeY / 3, hexSizeX, hexSizeY);
                        }
                        break;
                    case TEMPLE:
                        drawTemple(gc, building, selected, x, y, hexSizeX, hexSizeY);
                        break;
                    case TOWER:
                        drawTower(gc, building, selected, x, y, hexSizeX, hexSizeY);
                        break;
                }
            }
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
                    gc.setFill(Color.WHITE);
                    gc.fillText(hexStr, x, y);
                }
            }
        }

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
    }
}
