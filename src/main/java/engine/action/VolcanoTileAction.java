package engine.action;

import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.Orientation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class VolcanoTileAction extends TileAction {

    public VolcanoTileAction(VolcanoTile tile, Hex volcanoHex, Orientation orientation) {
        super(tile, volcanoHex, orientation);
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        FieldType leftFieldType = FieldType.valueOf(reader.readLine());
        FieldType rightFieldType = FieldType.valueOf(reader.readLine());
        VolcanoTile tile = new VolcanoTile(leftFieldType, rightFieldType);
        Orientation orientation = Orientation.valueOf(reader.readLine());
        return new VolcanoTileAction(tile, Hex.at(line, diag), orientation);
    }
}
