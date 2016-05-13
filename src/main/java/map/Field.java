package map;

import data.BuildingType;
import data.FieldType;
import data.PlayerColor;

import java.util.Optional;

public class Field {

    private final FieldType type;
    private final int level;
    private final Orientation orientation;
    private final FieldBuilding building;

    public Field(FieldType type, int level, Orientation orientation) {
        this.type = type;
        this.level = level;
        this.orientation = orientation;

        this.building = null;
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

    public Optional<FieldBuilding> getBuilding() {
        return Optional.ofNullable(building);
    }
}
