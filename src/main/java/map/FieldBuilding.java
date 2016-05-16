package map;

import data.BuildingType;
import data.PlayerColor;

import static com.google.common.base.Preconditions.checkState;

public class FieldBuilding {

    private final BuildingType type;
    private final PlayerColor color;

    FieldBuilding(BuildingType type, PlayerColor color) {
        this.type = type;
        this.color = color;
    }

    public BuildingType getType() {
        return type;
    }

    public PlayerColor getColor() {
        checkState(type != BuildingType.NONE, "Can't have a color with BuildingType.NONE");
        return color;
    }
}
