package map;

public interface HexMap<E> extends Iterable<Hex> {

    static <E> HexMap<E> create() {
        return new HexMapImpl<>();
    }

    boolean contains(Hex hex);

    E get(Hex hex);

    E getOrDefault(Hex hex, E fallbackValue);

    void put(Hex hex, E element);
}
