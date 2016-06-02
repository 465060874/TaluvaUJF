package engine.action;

import com.google.common.collect.ComparisonChain;
import data.BuildingType;
import data.PlayerColor;
import map.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkArgument;

public class PlaceBuildingAction implements BuildingAction<PlaceBuildingAction> {

    private final PlayerColor color;
    private final boolean isNew;
    private final BuildingType type;
    private final Hex hex;

    public PlaceBuildingAction(PlayerColor color, boolean isNew, BuildingType type, Hex hex) {
        checkArgument(type != BuildingType.NONE);
        this.isNew = isNew;
        this.color = color;
        this.type = type;
        this.hex = hex;
    }

    @Override
    public PlayerColor getColor() {
        return color;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public BuildingType getType() {
        return type;
    }

    public Hex getHex() {
        return hex;
    }

    @Override
    public int compareTo(PlaceBuildingAction o) {
        return ComparisonChain.start()
                .compare(type, o.type)
                .compare(hex.getLine(), o.hex.getLine())
                .compare(hex.getDiag(), o.hex.getDiag())
                .result();
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
        return new PlaceBuildingAction(PlayerColor.YELLOW, false, type, Hex.at(line, diag));
    }
}
