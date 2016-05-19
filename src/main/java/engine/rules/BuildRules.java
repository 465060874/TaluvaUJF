package engine.rules;

import data.BuildingType;
import data.PlayerColor;
import engine.Engine;
import map.*;

import static com.google.common.base.Preconditions.checkArgument;

public class BuildRules {

    public static boolean validate(Engine engine, BuildingType type, Hex hex) {
        Island island = engine.getIsland();
        checkArgument(type != BuildingType.NONE);

        final Field field = island.getField(hex);
        if (!isBuildable(field)) {
            return false;
        }

        if (hasEnoughBuilding(engine, type)) {
            return false;
        }

        PlayerColor color = engine.getCurrentPlayer().getColor();
        switch (type) {
            case TEMPLE: return isTempleInVillageOf3(hex, island, color);
            case TOWER: return isTowerAtLevel3(field) && isTowerInVillage(hex, island, color);
            case HUT: return isFieldAtLevel1(field);
        }

        throw new IllegalStateException();
    }

    static boolean hasEnoughBuilding(Engine engine, BuildingType type) {
        return engine.getCurrentPlayer().getBuildingCount(type) == 0;
    }

    static boolean isBuildable(Field field) {
        return field != Field.SEA
                && field.getType().isBuildable()
                && field.getBuilding().getType() == BuildingType.NONE;
    }

    static boolean isTempleInVillageOf3(Hex hex, Island island, PlayerColor color) {
        for (Hex neighbor : hex.getNeighborhood()) {
            FieldBuilding neighborBuilding = island.getField(neighbor).getBuilding();
            if (neighborBuilding.getType() != BuildingType.NONE
                    && neighborBuilding.getColor() == color) {
                final Village village = island.getVillage(neighbor);
                if (!village.hasTemple() && village.getHexSize() > 2) {
                    return true;
                }
            }
        }

        return false;
    }

    static boolean isTowerInVillage(Hex hex, Island island, PlayerColor color) {
        for (Hex neighbor : hex.getNeighborhood()) {
            FieldBuilding neighborBuilding = island.getField(neighbor).getBuilding();
            if (neighborBuilding.getType() != BuildingType.NONE
                    && neighborBuilding.getColor() == color) {
                final Village village = island.getVillage(hex);
                if (!village.hasTower()) {
                    return true;
                }
            }
        }

        return false;
    }

    static boolean isTowerAtLevel3(Field field) {
        return field.getLevel() >= 3;
    }

    static boolean isFieldAtLevel1(Field field) {
        return field.getLevel() == 1;
    }
}
