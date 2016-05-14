package map;

import data.BuildingType;
import data.PlayerColor;

public class FieldBuilding {

    private final BuildingType type;
    private final PlayerColor color;
    private final int count;

    FieldBuilding(PlayerColor color, BuildingType type, int count) {
        this.type = type;
        this.color = color;
        this.count = count;
    }

    public PlayerColor getColor() {
        return color;
    }

    public BuildingType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}
