package map;

import java.util.Set;

public interface HexMap<E> {

    static <E> HexMap<E> create() {
        return new HexMapImpl<>();
    }

    boolean contains(Hex hex);

    E get(Hex hex);

    E getOrDefault(Hex hex, E fallbackValue);

    E put(Hex hex, E element);

    E remove(Hex hex);

    int size();

    Set<Hex> hexes();

    HexMap<E> copy();

    double getHashFactor();
}
