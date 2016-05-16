package map;

import com.google.common.collect.ListMultimap;
import data.FieldType;
import data.PlayerColor;

public interface Village {

    PlayerColor getColor();

    int getFieldSize();

    Iterable<Hex> getHexes();

    ListMultimap<FieldType, Hex> getExpandableHexes();

    boolean hasTemple();

    boolean hasTower();
}