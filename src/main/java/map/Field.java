package map;

import data.BuildingType;
import data.FieldType;

public class Field {

    static Field SEA = new Field(0, null, Orientation.NORTH);

    private final int level;
    private final FieldType type;
    private final Orientation orientation;
    private final FieldBuilding building;

    Field(int level, FieldType type, Orientation orientation) {
        this(level, type, orientation, FieldBuilding.of(BuildingType.NONE, null));
    }

    Field(int level, FieldType type, Orientation orientation, FieldBuilding building) {
        this.level = level;
        this.type = type;
        this.orientation = orientation;
        this.building = building;
    }

    public int getLevel() {
        return level;
    }

    public FieldType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public FieldBuilding getBuilding() {
        return building;
    }
}
