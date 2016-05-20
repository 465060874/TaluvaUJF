package engine.action;

import map.Hex;
import map.Orientation;

import java.util.UUID;

public class VolcanoTileAction implements TileAction {

    private final UUID stepUUID;
    private final Hex volcanoHex;
    private final Orientation orientation;

    public VolcanoTileAction(UUID stepUUID, Hex volcanoHex, Orientation orientation) {
        this.stepUUID = stepUUID;
        this.volcanoHex = volcanoHex;
        this.orientation = orientation;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public Hex getVolcanoHex() {
        return volcanoHex;
    }

    public Hex getLeftHex() {
        return volcanoHex.getLeftNeighbor(orientation);
    }

    public Hex getRightHex() {
        return volcanoHex.getRightNeighbor(orientation);
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
