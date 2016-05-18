package engine.action;

import map.Hex;
import map.Orientation;

public class VolcanoPlacement implements Placement {

    private final Hex volcanoHex;
    private final Orientation orientation;

    public VolcanoPlacement(Hex volcanoHex, Orientation orientation) {
        this.volcanoHex = volcanoHex;
        this.orientation = orientation;
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
