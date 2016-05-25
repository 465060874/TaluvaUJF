package ui.island;

import map.Hex;
import map.Neighbor;

public class Grid {

    private static final double HEX_RADIUS_X = 60d;
    private static final double HEX_RADIUS_Y = 60d * 0.8d;
    private static final double HEX_HEIGHT = 10d;
    private static final double WEIRD_RATIO = Math.cos(Math.toRadians(30d));

    private double ox;
    private double oy;
    private double scale;

    Grid(double ox, double oy, double scale) {
        this.ox = ox;
        this.oy = oy;
        this.scale = scale;
    }

    public double getOx() {
        return ox;
    }

    public double getOy() {
        return oy;
    }

    void translate(double ox, double oy) {
        this.ox += ox;
        this.oy += oy;
    }

    public double getScale() {
        return scale;
    }

    void scale(double factor) {
        this.scale *= factor;
    }

    public double getHexRadiusX() {
        return HEX_RADIUS_X * scale;
    }

    public double getHexHalfWidth() {
        return getHexRadiusX() * WEIRD_RATIO;
    }

    public double getHexRadiusY() {
        return HEX_RADIUS_Y * scale;
    }

    public double getHexHeight() {
        return HEX_HEIGHT * scale;
    }

    Hex xyToHex(double x, double y, double width, double height) {
        double x2 = x - (width / 2 - ox);
        double y2 = y - (height / 2 - oy);
        double hexHalfSizeY = getHexRadiusY();

        x2 = x2 / (getHexHalfWidth() * 2);
        double t1 = (y2 + hexHalfSizeY) / hexHalfSizeY;
        double t2 = Math.floor(x2 + t1);
        double line = Math.floor((Math.floor(t1 - x2) + t2) / 3);
        double diag = Math.floor((Math.floor(2 * x2 + 1) + t2) / 3) - line;

        return Hex.at((int) line, (int) diag);
    }

    double hexToX(Hex hex, double width) {
        int diag = hex.getDiag();
        int line = hex.getLine();
        double centerX = width / 2 - ox;
        return centerX + lineDiagToX(line, diag);
    }

    double neighborToXOffset(Neighbor neighbor) {
        return lineDiagToX(neighbor.getLineOffset(), neighbor.getDiagOffset());
    }

    private double lineDiagToX(int line, int diag) {
        double hexHalfWidth = getHexHalfWidth();
        return diag * 2 * hexHalfWidth + line * hexHalfWidth;
    }

    double hexToY(Hex hex, double height) {
        int line = hex.getLine();
        double centerY = height / 2 - oy;
        return centerY + lineToY(line);
    }

    double neighborToYOffset(Neighbor neighbor) {
        return lineToY(neighbor.getLineOffset());
    }

    private double lineToY(int line) {
        double hexSizeY = getHexRadiusY();
        return line * hexSizeY + line * hexSizeY / 2;
    }

}
