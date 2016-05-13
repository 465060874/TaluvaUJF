package map;

import data.BuildingType;
import data.PlayerColor;

public class FieldBuilding {

    private final PlayerColor buildingColor;
    private final BuildingType buildingType;
    private final int buildingCount;

    public FieldBuilding(PlayerColor buildingColor, BuildingType buildingType, int buildingCount) {
        this.buildingColor = buildingColor;
        this.buildingType = buildingType;
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
