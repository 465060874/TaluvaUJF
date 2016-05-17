package engine.action;

import data.FieldType;
import map.Hex;
import map.Village;

import java.util.List;

public class ExpandAction {

    private final Village village;
    private final FieldType fieldType;

    public ExpandAction(Village village, FieldType fieldType) {
        this.village = village;
        this.fieldType = fieldType;
    }

    public Village getVillage() {
        return village;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public List<Hex> getExpandHexes() {
        return village.getExpandableHexes().get(fieldType);
    }
}
