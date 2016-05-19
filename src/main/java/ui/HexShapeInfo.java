package ui;

import com.google.common.collect.ComparisonChain;
import map.Field;

public class HexShapeInfo implements Comparable<HexShapeInfo> {

    Field field;
    boolean isPlacement;

    double x;
    double y;
    double sizeX;
    double sizeY;
    double scale;

    @Override
    public int compareTo(HexShapeInfo o) {
        return ComparisonChain.start()
            .compare(y, o.y)
            .compare(x, o.x)
            .result();
    }
}
