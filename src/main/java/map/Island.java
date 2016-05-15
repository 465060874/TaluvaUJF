package map;

import data.PlayerColor;
import data.VolcanoTile;

public interface Island {

    Field getField(Hex hex);

    Iterable<Hex> getCoast();

    Iterable<Hex> getFields();

    Iterable<Hex> getVolcanos();

    Iterable<Village> getVillages(PlayerColor color);

    void putTile(VolcanoTile tile, Hex hex, Orientation orientation);
}
