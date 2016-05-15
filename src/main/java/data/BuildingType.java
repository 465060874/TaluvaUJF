package data;

public enum BuildingType {

    NONE(0, true),

    TEMPLE(3, false),

    TOWER(2, false),

    HUT(20, true);

    private final int initialCount;
    private final boolean destructible;

    BuildingType(int initialCount, boolean destructible) {
        this.initialCount = initialCount;
        this.destructible = destructible;
    }

    public int getInitialCount() {
        return initialCount;
    }

    public boolean isDestructible() {
        return destructible;
    }
}
