package map;

import data.PlayerColor;
import data.VolcanoTile;

public interface Island {

    static Island createEmpty() {
        return new IslandImpl();
    }

    boolean isEmpty();

    Field getField(Hex hex);

    Iterable<Hex> getCoast();

    Iterable<Hex> getFields();

    Iterable<Hex> getVolcanos();

    Village getVillage(Hex hex);

    Iterable<Village> getVillages(PlayerColor color);

    void putField(Hex hex, Field field);

    void putTile(VolcanoTile tile, Hex hex, Orientation orientation);

    void putBuilding(Hex hex, Building building);

    Island copy();

    double getHashFactor();
}
