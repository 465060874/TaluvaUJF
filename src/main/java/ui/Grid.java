package ui;

import javafx.scene.input.MouseEvent;
import map.Hex;

import static ui.HexShape.*;

public class Grid {

    double ox;
    double oy;
    double scale;

    Grid(double ox, double oy, double scale) {
        this.ox = ox;
        this.oy = oy;
        this.scale = scale;
    }

    private Hex pointToHex(double x, double y) {
        double hexWidth = HEX_SIZE_X * scale * WEIRD_RATIO;
        double hexHeight = HEX_SIZE_Y * scale;

        x = x / (hexWidth * 2);
        double t1 = (y + hexHeight) / hexHeight;
        double t2 = Math.floor(x + t1);
        double line = Math.floor((Math.floor(t1 - x) + t2) / 3);
        double diag = Math.floor((Math.floor(2 * x + 1) + t2) / 3) - line;

        return Hex.at((int) line, (int) diag);
    }

    public Hex getHex(MouseEvent event, double width, double height) {
        double x = event.getX() - (width / 2 - ox);
        double y = event.getY() - (height / 2 - oy);
        return  pointToHex(x, y);
    }

    public HexZone getHexZone(MouseEvent event, double width, double height) {
        Hex hex = getHex(event, width, height);
        double hexSizeX = HEX_SIZE_X * scale;
        double hexSizeY = HEX_SIZE_Y * scale;

        double x = event.getX() - (width / 2 - ox);
        double y = event.getY() - (height / 2 - oy);

        // Calcul du centre de la tuile
        double hexCenterX = hex.getDiag() * 2 * WEIRD_RATIO * hexSizeX + hex.getLine() * WEIRD_RATIO * hexSizeX;
        double hexCenterY = hex.getLine() * hexSizeY + hex.getLine() * hexSizeY / 2;

        double degree = Math.toDegrees(Math.atan((y - hexCenterY) / (x - hexCenterX)));

        // Calcul de la zone correspondante
        return x > hexCenterX
                ? HexZone.atEastOf(degree)
                : HexZone.atWestOf(degree);
    }

    double getOx() {
        return ox;
    }

    void setOx(double ox) {
        this.ox = ox;
    }

    double getOy() {
        return oy;
    }

    void setOy(double oy) {
        this.oy = oy;
    }

    double getScale() {
        return scale;
    }

    void setScale(double scale) {
        this.scale = scale;
    }
}
