package map;

public interface Village {

    int getFieldSize();

    Iterable<Hex> getHexes();

    Iterable<Hex> getNeighborsHexes();

    boolean hasTemple();

    boolean hasTower();

    boolean isInTheVillage(Hex hex);
}