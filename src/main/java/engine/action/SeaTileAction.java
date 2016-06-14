package engine.action;

import map.Hex;
import map.Orientation;

import java.io.BufferedReader;
import java.io.IOException;

public class SeaTileAction extends TileAction {

    public SeaTileAction(Hex volcanoHex, Orientation orientation) {
        super(volcanoHex, orientation);
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        Orientation orientation = Orientation.valueOf(reader.readLine());
        return new SeaTileAction(Hex.at(line, diag), orientation);
    }
 }
