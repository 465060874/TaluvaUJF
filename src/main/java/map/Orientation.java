package map;

/**
 * Les 6 orientations possibles pour un hexagone (orienté pointe en haut)
 */
/*
    Orientations :
    ....N.......NE....
    .....\....../.....
    ......\..../......
    .......\__/.......
    NW_____/  \_____SE
    .......\__/.......
    ......./..\.......
    ....../....\......
    .... /......\.....
    ...SW....... S....
 */
public enum Orientation {

    NORTH(Neighbor.SOUTH_WEST, Neighbor.SOUTH_EAST),

    NORTH_WEST(Neighbor.SOUTH_EAST, Neighbor.EAST),

    SOUTH_WEST(Neighbor.EAST, Neighbor.NORTH_EAST),

    SOUTH(Neighbor.NORTH_EAST, Neighbor.NORTH_WEST),

    SOUTH_EAST(Neighbor.NORTH_WEST, Neighbor.WEST),

    NORTH_EAST(Neighbor.WEST, Neighbor.SOUTH_WEST),
    ;

    final Neighbor leftNeighbor;
    final Neighbor rightNeighbor;

    Orientation(Neighbor leftNeighbor, Neighbor rightNeighbor) {
        this.leftNeighbor = leftNeighbor;
        this.rightNeighbor = rightNeighbor;
    }

    public Orientation leftRotation() {
        return values()[(ordinal() + 2) % values().length];
    }

    public Orientation rightRotation() {
        return values()[(ordinal() + values().length - 2) % values().length];
    }

    public Orientation nextClockWise() {
        return values()[(ordinal() + values().length - 1) % values().length];
    }

    public Orientation nextAntiClockWise() {
        return values()[(ordinal() + 1) % values().length];
    }
}
