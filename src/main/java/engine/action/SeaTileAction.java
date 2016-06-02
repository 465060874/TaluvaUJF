package engine.action;

import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import map.Hex;
import map.Orientation;

import java.io.BufferedReader;
import java.io.IOException;

public class SeaTileAction extends TileAction {

    public SeaTileAction(PlayerColor color, VolcanoTile tile, Hex volcanoHex, Orientation orientation) {
        super(color, tile, volcanoHex, orientation);
    }

    static Action doRead(BufferedReader reader) throws IOException {
        int line = Integer.valueOf(reader.readLine());
        int diag = Integer.valueOf(reader.readLine());
        FieldType leftFieldType = FieldType.valueOf(reader.readLine());
        FieldType rightFieldType = FieldType.valueOf(reader.readLine());
        VolcanoTile tile = new VolcanoTile(leftFieldType, rightFieldType);
        Orientation orientation = Orientation.valueOf(reader.readLine());
        return new SeaTileAction(PlayerColor.YELLOW, tile, Hex.at(line, diag), orientation);
    }
 }
