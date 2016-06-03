package engine.rules;

import engine.Engine;
import engine.action.BuildingAction;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import map.Hex;
import map.Village;

public class BuildingRules {

    public static Problem validate(Engine engine, BuildingAction action) {
        if (action instanceof PlaceBuildingAction) {
            PlaceBuildingAction placeBuildingAction = (PlaceBuildingAction) action;
            return PlaceBuildingRules.validate(engine,
                    placeBuildingAction.getType(),
                    placeBuildingAction.getHex());
        }
        else {
            ExpandVillageAction expandVillageAction = (ExpandVillageAction) action;

            Hex villageHex = expandVillageAction.getVillageHex();
            if (!engine.getIsland().getField(villageHex).hasBuilding()) {
                return Problem.NOT_A_VILLAGE;
            }

            Village village = engine.getIsland().getVillage(villageHex);
            return ExpandVillageRules.validate(engine,
                    village,
                    expandVillageAction.getFieldType());
        }
    }
}
