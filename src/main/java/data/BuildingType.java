package data;

public enum BuildingType {

    TEMPLE(3),

    TOWER(2),

    HUT(20);

    private final int initialCount;

    BuildingType(int initialCount) {
        this.initialCount = initialCount;
    }

    public int getInitialCount() {
        return initialCount;
    }
}
