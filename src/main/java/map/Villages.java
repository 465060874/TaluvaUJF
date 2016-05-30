package map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * An union find which keeps track of the village on an island
 */
class Villages {

    private final Island island;

    // map contains two type of objects :
    //   - An instance of Hex which is the parent of this hex in the union find
    //   - An instance of VillageImple2 (if the key Hex is a top-level parent)
    private final Map<Hex, Object> map;

    Villages(Island island) {
        this.island = island;
        this.map = new HashMap<>(HexMapImpl.INITIAL_CAPACITY);
    }

    Villages(Villages villages, Island island) {
        this.island = island;
        this.map = new HashMap<>();
        map.putAll(villages.map);
    }

    private Hex find(Hex hex) {
        Object value = map.getOrDefault(hex, null);
        if (!(value instanceof Hex)) {
            return hex;
        }

        // Recursively looks up for the top-level parent hex
        // and flattens the union-find tree while at it
        Hex root = find((Hex) value);
        map.put(hex, root);
        return root;
    }

    private VillageImpl2 create(Hex hex) {
        VillageImpl2 created = new VillageImpl2(island, hex);
        map.put(hex, created);
        return created;
    }

    private void union(Hex hex1, Hex hex2) {
        Hex root1 = find(hex1);
        Hex root2 = find(hex2);
        if (root1.equals(root2)) {
            return;
        }

        VillageImpl2 village1 = (VillageImpl2) map.get(root1);
        VillageImpl2 village2 = (VillageImpl2) map.get(root2);
        VillageImpl2 union = new VillageImpl2(village1, village2);

        // We store the parent with the less hexes as a child
        // of the one with the most.
        // This supposedly helps make the union-find tree more wide
        // than deep
        if (village1.hexes.size() > village2.hexes.size()) {
            map.put(root2, root1);
            map.put(root1, union);
        }
        else {
            map.put(root1, root2);
            map.put(root2, union);
        }
    }

    @SuppressWarnings("unchecked")
    Village get(Hex hex) {
        checkArgument(island.getField(hex).getBuilding().getType() != BuildingType.NONE,
                "Requesting village for a hex without a building");
        checkState(map.containsKey(hex), "Something has gone wrong");
        return (VillageImpl2) map.get(find(hex));
    }

    Iterable<Village> getAll(PlayerColor color) {
        return map.values().stream()
                .filter(o -> o instanceof VillageImpl2)
                .map(o -> (VillageImpl2) o)
                .filter(v -> v.getColor() == color)
                .collect(Collectors.toList());
    }

    void update(Hex hex) {
        create(hex);
        doUpdate(hex);
    }

    private void doUpdate(Hex hex) {
        Building building = island.getField(hex).getBuilding();
        if (building.getType() != BuildingType.NONE) {
            for (Hex neighbor : hex.getNeighborhood()) {
                Building neighborBuilding = island.getField(neighbor).getBuilding();
                if (neighborBuilding.getType() != BuildingType.NONE
                        && building.getColor() == neighborBuilding.getColor()) {
                    union(hex, neighbor);
                }
            }
        }
    }

    void reset(Hex... hexes) {
        Set<Hex> hexesToUpdate = new HashSet<>();
        for (Hex hex : hexes) {
            if (map.containsKey(hex)) {
                Hex root = find(hex);
                checkState(map.containsKey(root), "Something has gone wrong");
                Village village = (VillageImpl2) map.get(root);

                if (!hexesToUpdate.contains(hex)) {
                    hexesToUpdate.addAll(village.getHexes());
                }
            }
        }

        for (Hex hex : hexesToUpdate) {
            if (island.getField(hex).getBuilding().getType() != BuildingType.NONE) {
                create(hex);
            }
            else {
                map.remove(hex);
            }
        }

        for (Hex hex : hexesToUpdate) {
            doUpdate(hex);
        }
    }

    void populate() {
        for (Hex existing : island.getFields()) {
            Building building = island.getField(existing).getBuilding();
            if (building.getType() != BuildingType.NONE) {
                create(existing);
            }
        }

        for (Hex existing : island.getFields()) {
            Building building = island.getField(existing).getBuilding();
            if (building.getType() != BuildingType.NONE) {
                doUpdate(existing);
            }
        }
    }

    Villages copy(Island island) {
        return new Villages(this, island);
    }

    class VillageImpl2 implements Village {

        private final Island island;
        private final PlayerColor color;
        private final Set<Hex> hexes;
        private final boolean hasTemple;
        private final boolean hasTower;

        VillageImpl2(Island island, Hex hex) {
            this.island = island;
            Field field = island.getField(hex);
            this.color = field.getBuilding().getColor();
            this.hexes = ImmutableSet.of(hex);
            this.hasTemple = field.getBuilding().getType() == BuildingType.TEMPLE;
            this.hasTower = field.getBuilding().getType() == BuildingType.TOWER;
        }

        VillageImpl2(VillageImpl2 village1, VillageImpl2 village2) {
            this.island = village1.island;
            this.color = village1.color;
            this.hexes = ImmutableSet.<Hex>builder()
                    .addAll(village1.hexes)
                    .addAll(village2.hexes)
                    .build();
            this.hasTemple = village1.hasTemple || village2.hasTemple;
            this.hasTower = village1.hasTower || village2.hasTower;
        }

        @Override
        public PlayerColor getColor() {
            return color;
        }

        @Override
        public Set<Hex> getHexes() {
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
}

