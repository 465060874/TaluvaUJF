package map;

public enum Direction {

    NORTH_WEST(-1, 0),

    NORTH_EAST(-1, 1),

    WEST(0, -1),

    EAST(0, 1),

    SOUTH_WEST(1, -1),

    SOUTH_EAST(1, 0);

    private final int dl;
    private final int dd;

    Direction(int dl, int dd) {
        this.dl = dl;
        this.dd = dd;
    }

    public int getDl() {
        return dl;
    }

    public int getDd() {
        return dd;
    }
}
