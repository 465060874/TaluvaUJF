package map;

import com.google.common.collect.ImmutableList;

/**
 * Les 6 directions possibles pour les voisins d'un hexagone (orienté pointe en haut)
 */
/* _....../NE\......_
 *  \__...\__/...__/
 * _/NO\__/02\__/ E\_
 * .\__/01\__/12\__/.
 * ....\__/11\__/....
 * ..__/10\__/21\__..
 * _/O \__/20\__/SE\_
 *  \__/..\__/..\__/
 * _/...../  \.....\_
 * .......\SW/.......
 */
public enum Neighbor {

    NORTH_WEST(-1, 0),

    NORTH_EAST(-1, 1),

    WEST(0, -1),

    EAST(0, 1),

    SOUTH_WEST(1, -1),

    SOUTH_EAST(1, 0);

    private static final ImmutableList<Neighbor> LIST = ImmutableList.copyOf(values());

    final int lineOffset;
    final int diagOffset;

    Neighbor(int lineOffset, int diagOffset) {
        this.lineOffset = lineOffset;
        this.diagOffset = diagOffset;
    }

    public static Neighbor leftOf(Orientation orientation) {
        switch (orientation) {
            case NORTH:      return Neighbor.SOUTH_WEST;
            case NORTH_EAST: return Neighbor.WEST;
            case SOUTH_EAST: return Neighbor.NORTH_WEST;
            case SOUTH:      return Neighbor.NORTH_EAST;
            case SOUTH_WEST: return Neighbor.EAST;
            case NORTH_WEST: return Neighbor.SOUTH_EAST;
        }

        throw new IllegalArgumentException();
    }

    public static Neighbor rightOf(Orientation orientation) {
        switch (orientation) {
            case NORTH:      return Neighbor.SOUTH_EAST;
            case NORTH_EAST: return Neighbor.SOUTH_WEST;
            case SOUTH_EAST: return Neighbor.WEST;
            case SOUTH:      return Neighbor.NORTH_WEST;
            case SOUTH_WEST: return Neighbor.NORTH_EAST;
            case NORTH_WEST: return Neighbor.EAST;
        }

        throw new IllegalArgumentException();
    }


    public static ImmutableList<Neighbor> list() {
        return LIST;
    }
}
