package ui.island;

import com.google.common.collect.ComparisonChain;
import data.FieldType;
import map.Building;
import map.Orientation;
import theme.BuildingStyle;
import theme.HexStyle;

class HexToDraw implements Comparable<HexToDraw> {

    public final double x;
    public final double y;
    public final int level;

    final FieldType fieldType;
    final Orientation orientation;
    final HexStyle hexStyle;

    final Building building;
    final BuildingStyle buildingStyle;

    HexToDraw(double x, double y, int level,
              FieldType fieldType, Orientation orientation, HexStyle hexStyle,
              Building building, BuildingStyle buildingStyle) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.fieldType = fieldType;
        this.orientation = orientation;
        this.hexStyle = hexStyle;
        this.building = building;
        this.buildingStyle = buildingStyle;
    }

    @Override
    public int compareTo(HexToDraw o) {
        return ComparisonChain.start()
            .compare(y, o.y)
            .compare(level, o.level)
            .compare(x, o.x)
            .result();
    }
}
