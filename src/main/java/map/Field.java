package map;

import data.BuildingType;
import data.FieldType;

public class Field {

    private final FieldType type;
    private final int level;
    private final BuildingType buildingType;
    private final int buildingCount;

    public Field(FieldType type, int level, BuildingType buildingType, int buildingCount) {
        this.type = type;
        this.level = level;
        this.buildingType = buildingType;
        this.buildingCount = buildingCount;
    }

    public FieldType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public int getBuildingCount() {
        return buildingCount;
    }
}
