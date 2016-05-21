package engine.action;

import data.FieldType;
import map.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class ExpandVillageAction implements BuildingAction {

    private final Hex villageHex;
    private final FieldType fieldType;

    public ExpandVillageAction(Hex villageHex, FieldType fieldType) {
        this.villageHex = villageHex;
        this.fieldType = fieldType;
    }

    public Hex getVillageHex() {
        return villageHex;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(getClass().getSimpleName());
        writer.write('\n');
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
        return new ExpandVillageAction(Hex.at(line, diag), fieldType);
    }
}
