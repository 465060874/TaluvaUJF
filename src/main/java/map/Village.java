package map;

import java.util.Iterator;

public interface Village {

    Iterator<Coords> getCoords();

    boolean hasTemple();

    boolean hasTower();
}