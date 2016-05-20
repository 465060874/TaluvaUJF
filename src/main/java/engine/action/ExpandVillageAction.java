package engine.action;

import data.FieldType;
import map.Hex;
import map.Village;

import java.util.Set;
import java.util.UUID;

public class ExpandVillageAction implements BuildingAction {

    private final UUID stepUUID;
    private final Village village;
    private final FieldType fieldType;

    public ExpandVillageAction(UUID stepUUID, Village village, FieldType fieldType) {
        this.stepUUID = stepUUID;
        this.village = village;
        this.fieldType = fieldType;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public Village getVillage() {
        return village;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public Set<Hex> getExpandHexes() {
        return village.getExpandableHexes().get(fieldType);
    }
}
