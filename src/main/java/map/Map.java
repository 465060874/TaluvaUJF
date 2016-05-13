package map;

import data.PlayerColor;

import java.util.Iterator;
import java.util.Optional;

public interface Map {

    Optional<Field> getField(Coords c);

    Iterator<Coords> getSeaLevel();

    Iterator<Coords> getFields();

    Iterator<Coords> getVolcanos();

    Iterator getVillages(PlayerColor color);
}
