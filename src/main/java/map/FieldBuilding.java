package map;

import data.BuildingType;
import data.PlayerColor;

public class FieldBuilding {

    private final BuildingType buildingType;
    private final PlayerColor buildingColor;
    private final int buildingCount;

    public FieldBuilding(PlayerColor buildingColor, BuildingType buildingType, int buildingCount) {
        this.buildingType = buildingType;
        this.buildingColor = buildingColor;
        this.buildingCount = buildingCount;
    }

    public PlayerColor getBuildingColor() {
        return buildingColor;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public int getBuildingCount() {
        return buildingCount;
    }
}
