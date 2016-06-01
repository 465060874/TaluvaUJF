package engine.action;

import data.BuildingType;
import map.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkArgument;

public class PlaceBuildingAction implements BuildingAction {

    private final BuildingType type;
    private final Hex hex;

    public PlaceBuildingAction(BuildingType type, Hex hex) {
        checkArgument(type != BuildingType.NONE);
        this.type = type;
        this.hex = hex;
    }

    public BuildingType getType() {
        return type;
    }

    public Hex getHex() {
        return hex;
    }

    @Override
    public String toString() {
        return "PlaceBuilding(" + hex + "," + type + ")";
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(getClass().getSimpleName());
        writer.write('\n');
        writer.write(Integer.toString(hex.getLine()));
        writer.write('\n');
        writer.write(Integer.toString(hex.getDiag()));
        writer.write('\n');
        writer.write(type.name());
        writer.write('\n');
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        BuildingType type = BuildingType.valueOf(reader.readLine());
        return new PlaceBuildingAction(type, Hex.at(line, diag));
    }
}
