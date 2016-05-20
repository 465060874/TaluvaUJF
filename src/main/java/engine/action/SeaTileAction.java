package engine.action;

import map.Hex;
import map.Orientation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

public class SeaTileAction implements TileAction {

    private final UUID stepUUID;
    private final Hex coastHex;
    private final Orientation orientation;

    public SeaTileAction(UUID stepUUID, Hex coastHex, Orientation orientation) {
        this.stepUUID = stepUUID;
        this.coastHex = coastHex;
        this.orientation = orientation;
    }

    public UUID getStepUUID() {
        return stepUUID;
    }

    public Hex getHex1() {
        return coastHex;
    }

    public Hex getHex2() {
        return coastHex.getLeftNeighbor(orientation);
    }

    public Hex getHex3() {
        return coastHex.getRightNeighbor(orientation);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(coastHex.getLine());
        writer.write('\n');
        writer.write(coastHex.getDiag());
        writer.write('\n');
        writer.write(orientation.name());
        writer.write('\n');
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        Orientation orientation = Orientation.valueOf(reader.readLine());
        return new SeaTileAction(UUID.randomUUID(), Hex.at(line, diag), orientation);
    }
}
