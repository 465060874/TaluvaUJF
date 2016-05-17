package ui;

import com.google.common.collect.ImmutableList;
import map.Neighbor;

public enum HexZone {
    HOUR_0(0),
    HOUR_1(1),
    HOUR_2(2),
    HOUR_3(3),
    HOUR_4(4),
    HOUR_5(5),
    HOUR_6(6),
    HOUR_7(7),
    HOUR_8(8),
    HOUR_9(9),
    HOUR_10(10),
    HOUR_11(11);

    private static final ImmutableList<HexZone> LIST = ImmutableList.copyOf(values());

    final int hour;

    HexZone(int hour) {
        this.hour = hour;
    }

    public static ImmutableList<HexZone> list() {
        return LIST;
    }

    public Neighbor getNeighbour() {
        if (this.hour == 0 || this.hour == 1) {
            return Neighbor.NORTH_EAST;
        } else if (this.hour == 2 || this.hour == 3) {
            return Neighbor.SOUTH_EAST;
        } else if (this.hour == 4 || this.hour == 5) {
            return Neighbor.EAST;
        } else if (this.hour == 6 || this.hour == 7) {
            return Neighbor.SOUTH_WEST;
        } else if (this.hour == 8 || this.hour == 9) {
            return Neighbor.WEST;
        } else {
            return Neighbor.NORTH_WEST;
        }
    }
}
