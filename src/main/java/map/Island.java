package map;

import data.PlayerColor;
import data.VolcanoTile;

public interface Island {

    static Island createEmpty() {
        return new IslandImpl();
    }

    Field getField(Hex hex);

    Iterable<Hex> getCoast();

    Iterable<Hex> getFields();

    Iterable<Hex> getVolcanos();

    Village getVillage(Hex hex);

    Iterable<Village> getVillages(PlayerColor color);

    void putTile(VolcanoTile tile, Hex hex, Orientation orientation);
}
