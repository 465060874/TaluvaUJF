package engine.rules;

import data.BuildingType;
import data.FieldType;
import engine.Engine;
import map.Hex;
import map.Island;
import map.Village;

import java.util.Set;

public class ExpandVillageRules {

    public static boolean validate(Engine engine, Village village, FieldType fieldType) {
        Island island = engine.getIsland();
        Set<Hex> expansion = village.getExpandableHexes().get(fieldType);
        if (expansion.isEmpty()) {
            return false;
        }

        int hutsCount = 0;
        for (Hex hex : expansion) {
            hutsCount += island.getField(hex).getLevel();
        }

        return hutsCount <= engine.getCurrentPlayer().getBuildingCount(BuildingType.HUT);
    }
}
