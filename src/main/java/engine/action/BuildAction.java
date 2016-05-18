package engine.action;

import data.BuildingType;
import map.Hex;

public class BuildAction implements Action {

    private final BuildingType type;
    private final Hex hex;

    public BuildAction(BuildingType type, Hex hex) {
        this.type = type;
        this.hex = hex;
    }

    public BuildingType getType() {
        return type;
    }

    public Hex getHex() {
        return hex;
    }
}
