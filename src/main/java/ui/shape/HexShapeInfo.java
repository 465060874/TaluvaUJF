package ui.shape;

import com.google.common.collect.ComparisonChain;
import map.Field;
import ui.theme.PlacementState;

public class HexShapeInfo implements Comparable<HexShapeInfo> {

    public Field field;
    public PlacementState placementState;

    public double x;
    public double y;
    public double sizeX;
    public double sizeY;
    public double scale;

    @Override
    public int compareTo(HexShapeInfo o) {
        return ComparisonChain.start()
            .compare(y, o.y)
            .compare(field.getLevel(), o.field.getLevel())
            .compare(x, o.x)
            .result();
    }
}
