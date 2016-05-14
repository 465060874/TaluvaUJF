package map;

import data.BuildingType;
import data.FieldType;

public class Field {

    public static Field SEA = new Field(0, null, Orientation.NORTH);

    private final int level;
    private final FieldType type;
    private final Orientation orientation;
    private final FieldBuilding building;

    Field(int level, FieldType type, Orientation orientation) {
        this.type = type;
        this.level = level;
        this.orientation = orientation;

        this.building = new FieldBuilding(null, BuildingType.NONE, 0);
    }

    public FieldType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public FieldBuilding getBuilding() {
        return building;
    }
}
