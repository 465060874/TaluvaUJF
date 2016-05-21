package engine.action;

import map.Hex;
import map.Orientation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class VolcanoTileAction implements TileAction {

    private final Hex volcanoHex;
    private final Orientation orientation;

    public VolcanoTileAction(Hex volcanoHex, Orientation orientation) {
        this.volcanoHex = volcanoHex;
        this.orientation = orientation;
    }

    public Hex getVolcanoHex() {
        return volcanoHex;
    }

    public Hex getLeftHex() {
        return volcanoHex.getLeftNeighbor(orientation);
    }

    public Hex getRightHex() {
        return volcanoHex.getRightNeighbor(orientation);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(getClass().getSimpleName());
        writer.write('\n');
        writer.write(Integer.toString(volcanoHex.getLine()));
        writer.write('\n');
        writer.write(Integer.toString(volcanoHex.getDiag()));
        writer.write('\n');
        writer.write(orientation.name());
        writer.write('\n');
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        Orientation orientation = Orientation.valueOf(reader.readLine());
        return new VolcanoTileAction(Hex.at(line, diag), orientation);
    }
}
