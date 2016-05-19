package map;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
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
        Hex firstHex = hexes.iterator().next();
        return island.getField(firstHex).getBuilding().getColor();
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
    public SetMultimap<FieldType, Hex> getExpandableHexes() {
        ImmutableSetMultimap.Builder<FieldType, Hex> builder = ImmutableSetMultimap.builder();
        for (Hex hex : hexes) {
            final Iterable<Hex> neighborhood = hex.getNeighborhood();
            for (Hex neighbor : neighborhood) {
                Field field = island.getField(neighbor);
                if (field != Field.SEA
                        && field.getType().isBuildable()
                        && field.getBuilding().getType() == BuildingType.NONE) {
                    builder.put(field.getType(), neighbor);
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
