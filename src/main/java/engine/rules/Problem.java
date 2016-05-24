package engine.rules;

public enum Problem {

    // Sea/Volcano Placement
    NOT_ON_SEA_OR_VOLCANO,

    // Sea Placement
    NOT_ALL_ON_SEA,
    NOT_ADJACENT_TO_COAST,

    // Volcano Placement
    NOT_ON_VOLCANO,
    SAME_VOLCANO_ORIENTATION,
    NOT_ON_SAME_LEVEL,
    CANT_DESTROY_TOWER_OR_TEMPLE,
    CANT_DESTROY_VILLAGE;
}
