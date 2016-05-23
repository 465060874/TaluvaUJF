package ui;

import com.google.common.collect.ComparisonChain;
import map.Field;

public class HexShapeInfo implements Comparable<HexShapeInfo> {

    Field field;
    boolean isPlacement;
    boolean isPlacementValid;

    double x;
    double y;
    double sizeX;
    double sizeY;
    double scale;

    @Override
    public int compareTo(HexShapeInfo o) {
        return ComparisonChain.start()
            .compare(y, o.y)
            .compare(field.getLevel(), o.field.getLevel())
            .compare(x, o.x)
            .result();
    }
}
