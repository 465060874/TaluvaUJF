package engine.rules;

import data.BuildingType;
import data.FieldType;
import engine.Engine;
import map.Hex;
import map.Island;
import map.Village;

import java.util.Set;

import static engine.rules.Problem.EXPAND_NOT_ENOUGH_BUILDING;
import static engine.rules.Problem.EXPAND_NO_ADJACENT_TILE;

public class ExpandVillageRules {

    public static Problem validate(Engine engine, Village village, FieldType fieldType) {
        Island island = engine.getIsland();
        Set<Hex> expansion = village.getExpandableHexes().get(fieldType);
        if (expansion.isEmpty()) {
            return Problem.EXPAND_NO_ADJACENT_TILE;
        }

        int hutsCount = 0;
        for (Hex hex : expansion) {
            hutsCount += island.getField(hex).getLevel();
        }

        return hutsCount <= engine.getCurrentPlayer().getBuildingCount(BuildingType.HUT)
                ? Problem.NONE
                : Problem.EXPAND_NOT_ENOUGH_BUILDING;
    }
}
