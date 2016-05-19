package engine.action;

import map.Hex;
import map.Orientation;

import java.util.UUID;

public class SeaPlacement implements Placement {

    private final UUID stepUUID;
    private final Hex coastHex;
    private final Orientation orientation;

    public SeaPlacement(UUID stepUUID, Hex coastHex, Orientation orientation) {
        this.stepUUID = stepUUID;
        this.coastHex = coastHex;
        this.orientation = orientation;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public Hex getHex1() {
        return coastHex;
    }

    public Hex getHex2() {
        return coastHex.getLeftNeighbor(orientation);
    }

    public Hex getHex3() {
        return coastHex.getRightNeighbor(orientation);
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
