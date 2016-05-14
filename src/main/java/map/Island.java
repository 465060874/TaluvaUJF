package map;

import data.PlayerColor;

import java.util.Optional;

public interface Island {

    Field getField(Hex c);

    Iterable<Hex> getCoast();

    Iterable<Hex> getFields();

    Iterable<Hex> getVolcanos();

    Iterable<Village> getVillages(PlayerColor color);
}
