package map;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
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
    public Iterable<Hex> getNeighborsHexes() {
        List<Hex> neighborHexes = new ArrayList<>();
        for (Hex hex : hexes) {
            final Iterable<Hex> neighborhood = hex.getNeighborhood();
            for (Hex neighbor : neighborhood) {
                if (!(hexes.contains(neighbor) || neighborHexes.contains(neighbor))) {
                    neighborHexes.add(neighbor);
                }
            }
        }
        return neighborHexes;
    }

    @Override
    public boolean hasTemple() {
        return hasTemple;
    }

    @Override
    public boolean hasTower() {
        return hasTower;
    }

    @Override
    public boolean isInTheVillage(Hex hex) {
        return hexes.contains(hex);
    }

}
