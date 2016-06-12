package map;

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

    final int lineOffset;
    final int diagOffset;

    Neighbor(int lineOffset, int diagOffset) {
        this.lineOffset = lineOffset;
        this.diagOffset = diagOffset;
    }

    public int getDiagOffset() {
        return diagOffset;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public static Neighbor leftOf(Orientation orientation) {
        return orientation.leftNeighbor;
    }

    public static Neighbor rightOf(Orientation orientation) {
        return orientation.rightNeighbor;
    }
}
