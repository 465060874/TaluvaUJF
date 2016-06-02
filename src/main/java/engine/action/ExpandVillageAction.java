package engine.action;

import com.google.common.collect.ComparisonChain;
import data.FieldType;
import data.PlayerColor;
import map.Hex;
import map.Island;
import map.Village;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class ExpandVillageAction implements BuildingAction<ExpandVillageAction> {

    private final PlayerColor color;
    private final boolean isNew;
    private final Hex villageHex;
    private final FieldType fieldType;

    public ExpandVillageAction(PlayerColor color, boolean isNew, Village village, FieldType fieldType) {
        this(color, isNew, village.getHexes().iterator().next(), fieldType);
    }

    public ExpandVillageAction(PlayerColor color, boolean isNew, Hex villageHex, FieldType fieldType) {
        this.color = color;
        this.isNew = isNew;
        this.villageHex = villageHex;
        this.fieldType = fieldType;
    }

    @Override
    public PlayerColor getColor() {
        return color;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Hex getVillageHex() {
        return villageHex;
    }

    public Village getVillage(Island island) {
        return island.getVillage(villageHex);
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    @Override
    public int compareTo(ExpandVillageAction o) {
        return ComparisonChain.start()
                .compare(villageHex.getLine(), o.villageHex.getLine())
                .compare(villageHex.getDiag(), o.villageHex.getDiag())
                .compare(fieldType, o.fieldType)
                .result();
    }

    @Override
    public String toString() {
        return "ExpandVillage(" + villageHex + "," + fieldType + ")";
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
        return new ExpandVillageAction(PlayerColor.YELLOW, false, Hex.at(line, diag), fieldType);
    }


}
