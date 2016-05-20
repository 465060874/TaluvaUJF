package engine.action;

import data.FieldType;
import map.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

public class ExpandVillageAction implements BuildingAction {

    private final UUID stepUUID;
    private final Hex villageHex;
    private final FieldType fieldType;

    public ExpandVillageAction(UUID stepUUID, Hex villageHex, FieldType fieldType) {
        this.stepUUID = stepUUID;
        this.villageHex = villageHex;
        this.fieldType = fieldType;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public Hex getVillageHex() {
        return villageHex;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(Integer.toString(villageHex.getLine()));
        writer.write('\n');
        writer.write(Integer.toString(villageHex.getDiag()));
        writer.write('\n');
        writer.write(fieldType.name());
        writer.write('\n');
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        FieldType fieldType = FieldType.valueOf(reader.readLine());
        return new ExpandVillageAction(UUID.randomUUID(), Hex.at(line, diag), fieldType);
    }
}
