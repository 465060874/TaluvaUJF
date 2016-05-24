package engine.rules;

import data.FieldType;
import data.VolcanoTile;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

public class PlacementRules {

    public static Problems validate(Island island, VolcanoTile tile, Hex hex, Orientation orientation) {
        Field field = island.getField(hex);
        if (field == Field.SEA) {
            return SeaPlacementRules.validate(island, tile, hex, orientation);
        }
        else if (field.getType() == FieldType.VOLCANO) {
            return VolcanoPlacementRules.validate(island, tile, hex, orientation);
        }
        else {
            return Problems.of(Problem.NOT_ON_SEA_OR_VOLCANO);
        }
    }
}
