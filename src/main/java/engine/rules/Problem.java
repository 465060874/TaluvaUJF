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
    CANT_DESTROY_VILLAGE,

    // PlaceBuilding
    NOT_BUILDABLE,
    PLACE_BUILDING_NOT_ENOUGH_BUILDINGS,
    HUT_TOO_HIGH,
    TEMPLE_NOT_IN_VILLAGE_OF_3,
    TOWER_NOT_HIGH_ENOUGH,
    TOWER_NOT_IN_VILLAGE,

    // ExpandVillage
    EXPAND_NO_ADJACENT_TILE,
    EXPAND_NOT_ENOUGH_BUILDING;
}
