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

    public static Problem validate(Engine engine, BuildingType type, Hex hex) {
        Island island = engine.getIsland();
        checkArgument(type != BuildingType.NONE);

        final Field field = island.getField(hex);
        if (field == Field.SEA) {
            return Problem.CANT_BUILD_ON_SEA;
        }
        if (!field.getType().isBuildable()) {
            return Problem.CANT_BUILD_ON_VOLCANO;
        }
        if (field.hasBuilding()) {
            return Problem.CANT_BUILD_ON_EXISTING_BUILDING;
        }

        if (!hasEnoughBuilding(engine, type)) {
            return Problem.PLACE_BUILDING_NOT_ENOUGH_BUILDINGS;
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

    static Problem validateTemple(Hex hex, Island island, PlayerColor color) {
        for (Hex neighbor : hex.getNeighborhood()) {
            Field neighborField = island.getField(neighbor);
            if (neighborField.hasBuilding(color)) {
                final Village village = island.getVillage(neighbor);
                if (!village.hasTemple() && village.getHexes().size() > 2) {
                    return Problem.NONE;
                }
            }
        }

        return Problem.TEMPLE_NOT_IN_VILLAGE_OF_3;
    }

    static Problem validateTower(Hex hex, Island island, PlayerColor color, Field field) {
        if (field.getLevel() < 3) {
            return Problem.TOWER_NOT_HIGH_ENOUGH;
        }

        for (Hex neighbor : hex.getNeighborhood()) {
            Field neighborField = island.getField(neighbor);
            if (neighborField.hasBuilding(color)) {
                final Village village = island.getVillage(neighbor);
                if (!village.hasTower()) {
                    return Problem.NONE;
                }
            }
        }

        return Problem.TOWER_NOT_IN_VILLAGE;
    }

    static Problem validateHut(Field field) {
        return field.getLevel() == 1
                ? Problem.NONE
                : Problem.HUT_TOO_HIGH;
    }
}
