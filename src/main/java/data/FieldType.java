package data;

public enum FieldType {

    VOLCANO(false),

    JUNGLE(true),

    CLEARING(true),

    SAND(true),

    ROCK(true),

    LAKE(true);

    private final boolean buildable;

    FieldType(boolean buildable) {
        this.buildable = buildable;
    }

    /**
     * Indique s'il est possible de construire sur ce type de terrain
     */
    public boolean isBuildable() {
        return buildable;
    }
}
