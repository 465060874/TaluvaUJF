package engine.rules;

import data.BuildingType;
import data.PlayerColor;
import engine.Engine;
import map.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Validation des placements de batiment
 */
public class PlaceBuildingRules {

    public static Problems validate(Engine engine, BuildingType type, Hex hex) {
        Island island = engine.getIsland();
        checkArgument(type != BuildingType.NONE);

        final Field field = island.getField(hex);
        if (!isBuildable(field)) {
            return Problems.of(Problem.NOT_BUILDABLE);
        }

        if (!hasEnoughBuilding(engine, type)) {
            return Problems.of(Problem.PLACE_BUILDING_NOT_ENOUGH_BUILDINGS);
        }

        PlayerColor color = engine.getCurrentPlayer().getColor();
        switch (type) {
            case TEMPLE: return validateTemple(hex, island, color);
            case TOWER: return validateTower(hex, island, color, field);
            case HUT: return validateHut(field);
        }

        throw new IllegalStateException();
    }

    static boolean hasEnoughBuilding(Engine engine, BuildingType type) {
        return engine.getCurrentPlayer().getBuildingCount(type) > 0;
    }

    static boolean isBuildable(Field field) {
        return field != Field.SEA
                && field.getType().isBuildable()
                && !field.hasBuilding();
    }

    static Problems validateTemple(Hex hex, Island island, PlayerColor color) {
        for (Hex neighbor : hex.getNeighborhood()) {
            Field neighborField = island.getField(neighbor);
            if (neighborField.hasBuilding(color)) {
                final Village village = island.getVillage(neighbor);
                if (!village.hasTemple() && village.getHexes().size() > 2) {
                    return Problems.of();
                }
            }
        }

        return Problems.of(Problem.TEMPLE_NOT_IN_VILLAGE_OF_3);
    }

    static Problems validateTower(Hex hex, Island island, PlayerColor color, Field field) {
        if (field.getLevel() < 3) {
            return Problems.of(Problem.TOWER_NOT_HIGH_ENOUGH);
        }

        for (Hex neighbor : hex.getNeighborhood()) {
            Field neighborField = island.getField(neighbor);
            if (neighborField.hasBuilding(color)) {
                final Village village = island.getVillage(neighbor);
                if (!village.hasTower()) {
                    return Problems.of();
                }
            }
        }

        return Problems.of(Problem.TOWER_NOT_IN_VILLAGE);
    }

    static Problems validateHut(Field field) {
        return field.getLevel() == 1
                ? Problems.of()
                : Problems.of(Problem.HUT_TOO_HIGH);
    }
}
