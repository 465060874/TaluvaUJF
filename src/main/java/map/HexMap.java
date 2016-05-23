package map;

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

    Iterable<Hex> hexes();

    Iterable<E> values();

    HexMap<E> copy();
}
