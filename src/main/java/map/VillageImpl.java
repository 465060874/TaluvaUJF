package map;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;

import java.util.List;

class VillageImpl implements Village {

    private final Island island;
    private final List<Hex> hexes;
    private final boolean hasTemple;
    private final boolean hasTower;

    VillageImpl(Island island, List<Hex> hexes, boolean hasTemple, boolean hasTower) {
        this.island = island;
        this.hexes = hexes;
        this.hasTemple = hasTemple;
        this.hasTower = hasTower;
    }

    @Override
    public PlayerColor getColor() {
        return island.getField(hexes.get(0)).getBuilding().getColor();
    }

    @Override
    public int getHexSize() {
        return hexes.size();
    }

    @Override
    public Iterable<Hex> getHexes() {
        return hexes;
    }

    @Override
    public ListMultimap<FieldType, Hex> getExpandableHexes() {
        ImmutableListMultimap.Builder<FieldType, Hex> builder = ImmutableListMultimap.builder();
        for (Hex hex : hexes) {
            final Iterable<Hex> neighborhood = hex.getNeighborhood();
            for (Hex neighbor : neighborhood) {
                Field field = island.getField(hex);
                if (field != Field.SEA
                        && field.getType().isBuildable()
                        && field.getBuilding().getType() == BuildingType.NONE) {
                    builder.put(field.getType(), hex);
                }
            }
        }

        return builder.build();
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
