package engine.action;

import data.BuildingType;
import map.Hex;

import java.util.UUID;

public class PlaceBuildingAction implements BuildingAction {

    private final UUID stepUUID;
    private final BuildingType type;
    private final Hex hex;

    public PlaceBuildingAction(UUID stepUUID, BuildingType type, Hex hex) {
        this.stepUUID = stepUUID;
        this.type = type;
        this.hex = hex;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public BuildingType getType() {
        return type;
    }

    public Hex getHex() {
        return hex;
    }
}