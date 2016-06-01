package ui.island;

import data.BuildingType;

public class WheelOfChoiceBuilding {
    final BuildingType buildingType;
    final boolean valid;

    public WheelOfChoiceBuilding(BuildingType buildingType, boolean valid) {
        this.buildingType = buildingType;
        this.valid = valid;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public boolean isValid() {
        return valid;
    }
}
