package map;

import com.google.common.collect.*;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.Buildings;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkArgument;
import static map.Field.SEA;

class IslandImpl implements Island {

    private final HexMap<Field> map;

    IslandImpl() {
        this.map = HexMap.create();
    }

    @Override
    public Field getField(Hex hex) {
        return map.getOrDefault(hex, SEA);
    }

    @Override
    public Iterable<Hex> getCoast() {
        ImmutableList.Builder<Hex> builder = ImmutableList.builder();

        for (Hex hex : map) {
            for (Hex neighbor : hex.getNeighborhood()) {
                if (!map.contains(neighbor)) {
                    builder.add(hex);
                }
            }
        }

        return builder.build();
    }

    @Override
    public Iterable<Hex> getFields() {
        return Iterables.unmodifiableIterable(map);
    }

    @Override
    public Iterable<Hex> getVolcanos() {
        return Iterables.filter(map, (hex) -> map.get(hex).getType() == FieldType.VOLCANO);
    }

    @Override
    public Village getVillage(Hex from) {
        Field fromField = map.getOrDefault(from, SEA);
        checkArgument(fromField.getBuilding().getType() == BuildingType.NONE, "Village without a building");
        PlayerColor fromColor = fromField.getBuilding().getColor();

        HexMap<Boolean> queued = HexMap.create();
        Queue<Hex> queue = new ArrayDeque<>(map.size());
        queue.add(from);

        ImmutableList.Builder<Hex> builder = ImmutableList.builder();
        boolean hasTower = false;
        boolean hasTemple = false;

        while (!queue.isEmpty()) {
            Hex hex = queue.remove();
            FieldBuilding building = map.getOrDefault(hex, SEA).getBuilding();
            if (building.getType() == BuildingType.NONE
                    || building.getColor() != fromColor) {
                continue;
            }

            hasTower = hasTower || building.getType() == BuildingType.TOWER;
            hasTemple = hasTemple || building.getType() == BuildingType.TEMPLE;

            builder.add(hex);
            for (Hex neighbor : hex.getNeighborhood()) {
                if (!queued.getOrDefault(neighbor, false)) {
                    queued.put(neighbor, true);
                    queue.add(neighbor);
                }
            }
        }

        return new VillageImpl(builder.build(), hasTemple, hasTower);
    }

    @Override
    public Iterable<Village> getVillages(PlayerColor color) {
        // On regroupe les batiments en village avec un union find
        HexUnionFind unionFind = new HexUnionFind();

        for (Hex hex : map) {
            Field field = map.getOrDefault(hex, SEA);
            if (field.getBuilding().getCount() == 0
                    || field.getBuilding().getColor() != color) {
                continue;
            }

            for (Hex neighbor : hex.getNeighborhood()) {
                Field neighorField = map.getOrDefault(neighbor, SEA);

                // Exploration des couleurs voisines
                if (neighorField.getBuilding().getCount() > 0
                        || neighorField.getBuilding().getColor() == color) {
                    unionFind.union(hex, neighbor);
                }
            }
        }

        // Regroupement des villages par representant
        ListMultimap<Hex, Hex> villagesHexes = ArrayListMultimap.create();
        HexMap<Boolean> hasTemple = HexMap.create();
        HexMap<Boolean> hasTower = HexMap.create();

        for (Hex hex : map) {
            Hex parent = unionFind.find(hex);
            villagesHexes.put(parent, hex);

            if (map.get(hex).getBuilding().getType() == BuildingType.TEMPLE) {
                hasTemple.put(hex, true);
            }
            else if (map.get(hex).getBuilding().getType() == BuildingType.TOWER) {
                hasTower.put(hex, true);
            }
        }

        // Construction de la liste de villages
        ImmutableList.Builder<Village> builder = ImmutableList.builder();

        for (Map.Entry<Hex, List<Hex>> entry : Multimaps.asMap(villagesHexes).entrySet()) {
            builder.add(new VillageImpl(
                    ImmutableList.copyOf(entry.getValue()),
                    hasTemple.contains(entry.getKey()),
                    hasTower.contains(entry.getKey())));
        }

        return builder.build();
    }

    void putHex(Hex hex, Field field) {
        if (field == SEA) {
            return;
        }

        map.put(hex, field);
    }

    public void putTile(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex leftHex = hex.getLeftNeighbor(orientation);
        Hex rightHex = hex.getRightNeighbor(orientation);

        int level = getField(hex).getLevel() + 1;

        Field volcanoField = new Field(level, FieldType.VOLCANO, orientation);
        Field leftField = new Field(level, tile.getLeft(), orientation.leftRotation());
        Field rightField = new Field(level, tile.getRight(), orientation.rightRotation());

        putHex(hex, volcanoField);
        putHex(rightHex, rightField);
        putHex(leftHex, leftField);
    }

    @Override
    public void putBuilding(BuildingType buildingType, Hex hex, PlayerColor color) {
        final int level = map.get(hex).getLevel();
        final FieldType type = map.get(hex).getType();
        final Orientation orientation = map.get(hex).getOrientation();

        final int count = (buildingType == BuildingType.TEMPLE || buildingType == BuildingType.TOWER) ?
                1 : level;

        map.put(hex, new Field(level, type, orientation, new FieldBuilding(buildingType, color, count)));
    }
}