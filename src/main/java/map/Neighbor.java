package map;

import com.google.common.collect.ImmutableList;

/**
 * Les 6 directions possibles pour les voisins d'un hexagone (orienté pointe en haut)
 */
public enum Neighbor {

    NORTH_WEST(-1, 0),

    NORTH_EAST(-1, 1),

    WEST(0, -1),

    EAST(0, 1),

    SOUTH_WEST(1, -1),

    SOUTH_EAST(1, 0);

    private static final ImmutableList<Neighbor> LIST = ImmutableList.copyOf(values());

    final int diffLine;
    final int diffDiag;

    Neighbor(int diffLine, int diffDiag) {
        this.diffLine = diffLine;
        this.diffDiag = diffDiag;
    }

    public static ImmutableList<Neighbor> list() {
        return LIST;
    }
}
