package map;

import data.PlayerColor;

import java.util.Iterator;

public interface Map {

    Field getField(Coords c);

    Iterator<Coords> getEmptyFields();

    Iterator<Coords> getFields();

    Iterator<Coords> getVolcanos();

    Iterator<Village> getVillages(PlayerColor color);
}
