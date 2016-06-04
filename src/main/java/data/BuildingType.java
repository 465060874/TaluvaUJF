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

    /**
     * Retourne le nombre de pièces de ce type de batiments
     * que chaque joueur à au début du jeu
     */
    public int getInitialCount() {
        return initialCount;
    }

    /**
     * Indique si ce type de batiment peut être détruit par
     * un placement de tuile
     */
    public boolean isDestructible() {
        return destructible;
    }

    public BuildingType nextBuilding() {
        switch(this) {
            case HUT:
                return TEMPLE;
            case TEMPLE:
                return TOWER;
            case TOWER:
                return HUT;
            default:
                throw new IllegalArgumentException();
        }
    }
}
