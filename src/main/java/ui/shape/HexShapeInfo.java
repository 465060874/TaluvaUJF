package ui.shape;

import com.google.common.collect.ComparisonChain;
import data.FieldType;
import map.FieldBuilding;
import map.Orientation;
import ui.theme.PlacementState;

public class HexShapeInfo implements Comparable<HexShapeInfo> {

    public double x;
    public double y;
    public int level;
    public FieldType fieldType;
    public Orientation orientation;
    public FieldBuilding building;
    public PlacementState placementState;

    @Override
    public int compareTo(HexShapeInfo o) {
        return ComparisonChain.start()
            .compare(y, o.y)
            .compare(level, o.level)
            .compare(x, o.x)
            .result();
    }
}
