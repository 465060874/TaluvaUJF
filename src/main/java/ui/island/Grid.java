package ui.island;

import map.Hex;

public class Grid {

    public static final double HEX_SIZE_X = 60d;
    public static final double HEX_SIZE_Y = 60d * 0.8d;
    public static final double HEX_HEIGHT = 10d;
    public static final double WEIRD_RATIO = Math.cos(Math.toRadians(30d));

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

    public Hex getHex(double mx, double my, double width, double height) {
        double x = mx - (width / 2 - ox);
        double y = my - (height / 2 - oy);
        return  pointToHex(x, y);
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
