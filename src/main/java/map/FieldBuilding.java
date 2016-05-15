package map;

import data.BuildingType;
import data.PlayerColor;

import static com.google.common.base.Preconditions.checkState;

public class FieldBuilding {

    private final BuildingType type;
    private final PlayerColor color;
    private final int count;

    FieldBuilding(BuildingType type, PlayerColor color, int count) {
        this.type = type;
        this.color = color;
        this.count = count;
    }

    public BuildingType getType() {
        return type;
    }

    public PlayerColor getColor() {
        checkState(type != BuildingType.NONE, "Can't have a color with BuildingType.NONE");
        return color;
    }

    public int getCount() {
        return count;
    }
}
