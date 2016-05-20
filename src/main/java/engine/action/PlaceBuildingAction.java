package engine.action;

import data.BuildingType;
import map.Hex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

import static com.google.common.base.Preconditions.*;

public class PlaceBuildingAction implements BuildingAction {

    private final UUID stepUUID;
    private final BuildingType type;
    private final Hex hex;

    public PlaceBuildingAction(UUID stepUUID, BuildingType type, Hex hex) {
        checkArgument(type != BuildingType.NONE);
        this.stepUUID = stepUUID;
        this.type = type;
        this.hex = hex;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public BuildingType getType() {
        return type;
    }

    public Hex getHex() {
        return hex;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(type.name());
        writer.write('\n');
    }

    static Action doRead(BufferedReader reader) throws IOException {
        BuildingType type = BuildingType.valueOf(reader.readLine());
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        return new PlaceBuildingAction(UUID.randomUUID(), type, Hex.at(line, diag));
    }
}
