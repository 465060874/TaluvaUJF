package engine.action;

import map.Hex;
import map.Orientation;

public class SeaPlacement {

    private final Hex coastHex;
    private final Orientation orientation;

    public SeaPlacement(Hex coastHex, Orientation orientation) {
        this.coastHex = coastHex;
        this.orientation = orientation;
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
