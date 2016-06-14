package engine.rules;

import data.FieldType;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

public class TileRules {

    public static Problem validate(Island island, Hex hex, Orientation orientation) {
        Field field = island.getField(hex);
        if (field == Field.SEA) {
            return SeaTileRules.validate(island, hex, orientation);
        }
        else if (field.getType() == FieldType.VOLCANO) {
            return VolcanoTileRules.validate(island, hex, orientation);
        }
        else {
            return Problem.NOT_ON_SEA_OR_VOLCANO;
        }
    }
}
