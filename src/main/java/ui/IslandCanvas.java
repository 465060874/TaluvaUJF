package ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import map.*;

import java.util.List;

import static ui.BuildingShapes.drawBuilding;
import static ui.HexShape.WEIRD_RATIO;

class IslandCanvas extends Canvas {

    static final Color BG_COLOR = Color.web("5E81A2");

    static final Color BORDER_COLOR = Color.web("303030");
    static final Color BOTTOM_COLOR = Color.web("505050");

    private final Island island;
    private final boolean debug;
    private final HexShape hexShape;

    double ox;
    double oy;
    double scale;

    private Hex selectedHex;
    private HexZone selectedHexZone;

    // Variable de selection de la tuile
    private List<Hex> stackHexes;
    private final static int westGap = 3;
    private final static int eastGap = 9;
    private VolcanoTile tileStack;
    private Orientation tileStackRotation;

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
        this.selectedHexZone = null;
        this.stackHexes = ImmutableList.of();
        this.tileStackRotation = Orientation.NORTH;
        this.tileStack = new VolcanoTile(FieldType.CLEARING, FieldType.SAND);

        widthProperty().addListener(this::resize);
        heightProperty().addListener(this::resize);

        setOnMouseMoved(this::mouseMoved);
        setOnMouseClicked(this::mouseLeftClicked);
    }

    private void mouseLeftClicked (MouseEvent event) {
        if (MouseButton.SECONDARY.equals(event.getButton())) {
            tileStackRotation = tileStackRotation.clockWise().clockWise();
            redraw();
        }
    }

    private void mouseMoved(MouseEvent event) {
        double x = event.getX() - (getWidth() / 2 - ox);
        double y = event.getY() - (getHeight() / 2 - oy);

        Hex newSelectedHex = pointToHex(x, y);
        HexZone newSelectedHexZone = pointToHexZone(newSelectedHex, x, y);

        boolean redraw = !newSelectedHex.equals(selectedHex)
            || newSelectedHexZone != selectedHexZone;

        selectedHex = newSelectedHex;
        selectedHexZone = newSelectedHexZone;
        Orientation firstCandidate = newSelectedHexZone.getOrientation();
        Orientation candidate = firstCandidate;
        for (int i = 0; i < Orientation.values().length; i++) {
            ImmutableList<Hex> temporaryHex2 = ImmutableList.of(
                    newSelectedHex,
                    newSelectedHex.getLeftNeighbor(candidate),
                    newSelectedHex.getRightNeighbor(candidate));
            if (temporaryHex2.stream().allMatch(h -> island.getField(h) == Field.SEA)) {
                boolean found = false;
                Hex hex = temporaryHex2.get(0);
                for (Hex hex1 : hex.getNeighborhood()) {
                    if (island.getField(hex1) != Field.SEA) {
                        found = true;
                    }
                }
                if (!found) {
                    stackHexes = ImmutableList.of();
                    break;
                }

                stackHexes = temporaryHex2;
                break;
            }

            candidate = firstCandidate;
            if ((i & 1) == 0) {
                for (int j = 0; j < i / 2; j++) {
                    candidate = candidate.clockWise();
                }
            }
            else {
                for (int j = 0; j < i / 2; j++) {
                    candidate = candidate.antiClockWise();
                }
            }
        }


        if (redraw) {
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

    private HexZone pointToHexZone(Hex hex, double x, double y) {
        double hexSizeX = HexShape.HEX_SIZE_X * scale;
        double hexSizeY = HexShape.HEX_SIZE_Y * scale;

        // Calcul du centre de la tuile
        double hexCenterX = hex.getDiag() * 2 * WEIRD_RATIO * hexSizeX + hex.getLine() * WEIRD_RATIO * hexSizeX;
        double hexCenterY = hex.getLine() * hexSizeY + hex.getLine() * hexSizeY / 2;

        double degree = Math.toDegrees(Math.atan((y - hexCenterY) / (x - hexCenterX)));

        // Calcul de la zone correspondante
        // TODO Remove dirty %12
        return HexZone.at((int) Math.floor(x > hexCenterX ? (degree/30) + westGap : (degree/30) + eastGap) % 12);
    }

    private ImmutableList<Field> rotate() {
        int selectedLevel = island.getField(stackHexes.get(0)).getLevel() + 1;
        if (tileStackRotation == Orientation.NORTH) {
            return ImmutableList.of(Field.create(selectedLevel, FieldType.VOLCANO, tileStackRotation),
                    Field.create(selectedLevel, tileStack.getLeft(), tileStackRotation),
                    Field.create(selectedLevel, tileStack.getRight(), tileStackRotation));
        } else if (tileStackRotation == Orientation.SOUTH_EAST) {
            return ImmutableList.of(Field.create(selectedLevel, tileStack.getLeft(), tileStackRotation),
                    Field.create(selectedLevel, tileStack.getRight(), tileStackRotation),
                    Field.create(selectedLevel, FieldType.VOLCANO, tileStackRotation));
        } else {
            return ImmutableList.of(Field.create(selectedLevel, tileStack.getRight(), tileStackRotation),
                    Field.create(selectedLevel, FieldType.VOLCANO, tileStackRotation),
                    Field.create(selectedLevel, tileStack.getLeft(), tileStackRotation));
        }
    }

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

        ImmutableList<Field> selectedFields = ImmutableList.of();
        Iterable<Hex> allHexes = island.getFields();
        if (!stackHexes.isEmpty()) {
            allHexes = Iterables.concat(stackHexes, allHexes);
            selectedFields = rotate();
        }

        // Affichage de la map
        for (Hex hex : Hex.lineThenDiagOrdering().sortedCopy(allHexes)) {
            int line = hex.getLine();
            int diag = hex.getDiag();
            double x = centerX + diag * 2 * WEIRD_RATIO * hexSizeX + line * WEIRD_RATIO * hexSizeX;
            double y = centerY + line * hexSizeY + line * hexSizeY / 2;
            int temporaryHexIndex = stackHexes.indexOf(hex);
            boolean isTemporary = !selectedFields.isEmpty() && temporaryHexIndex >= 0;
            Field field = isTemporary
                    ? selectedFields.get(temporaryHexIndex)
                    : island.getField(hex);
            boolean selected = !isTemporary && selectedHex != null && hex.equals(selectedHex);

            hexShape.draw(gc, field, selected, x, y, hexSizeX, hexSizeY, scale);

            FieldBuilding building = field.getBuilding();
            if (building.getType() != BuildingType.NONE) {
                drawBuilding(gc, building, field.getLevel(), selected,
                        x, y - hexSizeY / 6, hexSizeX, hexSizeY);
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
                    gc.setFill(Color.BLACK);
                    gc.fillText(hexStr, x, y);
                }
            }
        }

        setTranslateX(-getWidth() / 3);
        setTranslateY(-getHeight() / 3);
    }


}
