package map;

import com.google.common.collect.*;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;

import java.util.List;
import java.util.Map;

public class IslandImpl implements Island {

    private final HexMap<Field> map;

    public IslandImpl() {
        this.map = HexMap.create();
    }

    @Override
    public Field getField(Hex hex) {
        return map.getOrDefault(hex, Field.SEA);
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
    public Iterable<Village> getVillages(PlayerColor color) {
        // On regroupe les batiments en village avec un union find
        HexUnionFind unionFind = new HexUnionFind();

        for (Hex hex : map) {
            Field field = map.getOrDefault(hex, Field.SEA);
            if (field.getBuilding().getCount() == 0
                    || field.getBuilding().getColor() != color) {
                continue;
            }

            for (Hex neighbor : hex.getNeighborhood()) {
                Field neighorField = map.getOrDefault(neighbor, Field.SEA);

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
        map.put(hex, field);
    }

    public void putTile(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex leftHex = hex.getLeftNeighbor(orientation);
        Hex rightHex = hex.getRightNeighbor(orientation);

        int level = getField(hex).getLevel() + 1;

        Field volcanoField = new Field(level, FieldType.VOLCANO, orientation);
        Field leftField = new Field(level, tile.getLeft(), orientation.leftRotation());
        Field rightField = new Field(level, tile.getRight(), orientation.rightRotation());

        map.put(hex, volcanoField);
        map.put(rightHex, rightField);
        map.put(leftHex, leftField);
    }
}
