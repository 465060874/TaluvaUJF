package ui;

import com.google.common.collect.ImmutableList;
import map.Neighbor;
import map.Orientation;

import java.security.InvalidParameterException;

/*
   _______
  /\11| 0/\
 /10\ | / 1\
/_9__\|/__2_\
\ 8  /|\  3 /
 \7 / | \ 4/
  \/_6|5_\/

 */
enum HexZone {
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

    private final int hour;

    HexZone(int hour) {
        this.hour = hour;
    }

    public static ImmutableList<HexZone> list() {
        return LIST;
    }

    public Neighbor getFrontNeighbour() {
        if (this.hour == 0 || this.hour == 1) {
            return Neighbor.NORTH_EAST;
        } else if (this.hour == 2 || this.hour == 3) {
            return Neighbor.EAST;
        } else if (this.hour == 4 || this.hour == 5) {
            return Neighbor.SOUTH_EAST;
        } else if (this.hour == 6 || this.hour == 7) {
            return Neighbor.SOUTH_WEST;
        } else if (this.hour == 8 || this.hour == 9) {
            return Neighbor.WEST;
        } else {
            return Neighbor.NORTH_WEST;
        }
    }

    public static HexZone at(int zone) {
        return values()[zone];
    }

    public Orientation getOrientation() {
        switch (this.ordinal()) {
            case 1: return Orientation.SOUTH_WEST;
            case 2: return Orientation.SOUTH_WEST;
            case 3: return Orientation.NORTH_WEST;
            case 4: return Orientation.NORTH_WEST;
            case 5: return Orientation.NORTH;
            case 6: return Orientation.NORTH;
            case 7: return Orientation.NORTH_EAST;
            case 8: return Orientation.NORTH_EAST;
            case 9: return Orientation.SOUTH_EAST;
            case 10: return Orientation.SOUTH_EAST;
            case 11: return Orientation.SOUTH;
            case 0: return Orientation.SOUTH;
            default:
                break;
        }

        throw new InvalidParameterException();
    }
}
