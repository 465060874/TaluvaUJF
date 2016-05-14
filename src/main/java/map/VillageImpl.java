package map;

import java.util.List;

class VillageImpl implements Village {

    private final List<Hex> hexes;
    private final boolean hasTemple;
    private final boolean hasTower;

    VillageImpl(List<Hex> hexes, boolean hasTemple, boolean hasTower) {
        this.hexes = hexes;
        this.hasTemple = hasTemple;
        this.hasTower = hasTower;
    }

    @Override
    public int getFieldSize() {
        return hexes.size();
    }

    @Override
    public Iterable<Hex> getHexes() {
        return hexes;
    }

    @Override
    public boolean hasTemple() {
        return hasTemple;
    }

    @Override
    public boolean hasTower() {
        return hasTower;
    }
}
