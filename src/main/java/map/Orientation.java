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

    NORTH,

    NORTH_WEST,

    SOUTH_WEST,

    SOUTH,

    SOUTH_EAST,

    NORTH_EAST,
    ;

    public Orientation leftRotation() {
         switch (this) {
            case NORTH:      return SOUTH_WEST;
            case SOUTH_WEST: return SOUTH_EAST;
            case SOUTH_EAST: return NORTH;

            case SOUTH:      return NORTH_EAST;
            case NORTH_WEST: return SOUTH;
            case NORTH_EAST: return NORTH_WEST;
        }

        throw new IllegalArgumentException();
    }

    public Orientation rightRotation() {
        switch (this) {
            case NORTH:      return SOUTH_EAST;
            case SOUTH_WEST: return NORTH;
            case SOUTH_EAST: return SOUTH_WEST;

            case SOUTH:      return NORTH_WEST;
            case NORTH_WEST: return NORTH_EAST;
            case NORTH_EAST: return SOUTH;
        }

        throw new IllegalArgumentException();
    }

    public Orientation clockWise() {
        return values()[(ordinal() + values().length - 1) % values().length];
    }

    public Orientation antiClockWise() {
        return values()[(ordinal() + 1) % values().length];
    }

    public Hex getFrontHex(Hex hex) {
        final int line = hex.getLine();
        final int diag = hex.getDiag();

        switch (this) {
            case NORTH:      return Hex.at(line - 2, diag + 1);
            case SOUTH_WEST: return Hex.at(line + 1, diag - 2);
            case SOUTH_EAST: return Hex.at(line + 1, diag + 1);

            case SOUTH:      return Hex.at(line + 2, diag - 1);
            case NORTH_WEST: return Hex.at(line - 1, diag - 1);
            case NORTH_EAST: return Hex.at(line - 1, diag + 2);
        }

        throw new IllegalArgumentException();
    }
}
