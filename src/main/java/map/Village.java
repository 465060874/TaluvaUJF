package map;

public interface Village {

    int getFieldSize();

    Iterable<Hex> getHexes();

    boolean hasTemple();

    boolean hasTower();
}