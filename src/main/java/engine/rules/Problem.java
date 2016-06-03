package engine.rules;

import java.util.function.Supplier;

public enum Problem {

    NONE,

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
    CANT_BUILD_ON_SEA,
    CANT_BUILD_ON_VOLCANO,
    CANT_BUILD_ON_EXISTING_BUILDING,
    PLACE_BUILDING_NOT_ENOUGH_BUILDINGS,
    HUT_TOO_HIGH,
    TEMPLE_NOT_IN_VILLAGE_OF_3,
    TOWER_NOT_HIGH_ENOUGH,
    TOWER_NOT_IN_VILLAGE,

    // ExpandVillage
    NOT_A_VILLAGE,
    EXPAND_NO_ADJACENT_TILE,
    EXPAND_NOT_ENOUGH_BUILDING;

    public boolean isValid() {
        return this == NONE;
    }

    public Problem and(Supplier<Problem> otherProblem) {
        return this == NONE
                ? otherProblem.get()
                : this;
    }
}
