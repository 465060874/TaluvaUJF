package map;

import com.google.common.collect.Iterables;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;

import static com.google.common.base.MoreObjects.firstNonNull;
import static map.Field.SEA;

class  IslandImpl implements Island {

    private final HexMap<Field> map;
    final Villages villages;

    IslandImpl() {
        this.map = HexMap.create();
        this.villages = new Villages(this);
    }

    private IslandImpl(IslandImpl island) {
        this.map = island.map.copy();
        this.villages = island.villages.copy(this);
    }

    @Override
    public Field getField(Hex hex) {
        return map.getOrDefault(hex, SEA);
    }

    @Override
    public Iterable<Hex> getCoast() {
        HexMap<Boolean> builder = HexMap.create();

        for (Hex hex : map.hexes()) {
            for (Hex neighbor : hex.getNeighborhood()) {
                if (!map.contains(neighbor)) {
                    builder.put(neighbor, true);
                }
            }
        }

        return builder.hexes();
    }

    @Override
    public Iterable<Hex> getFields() {
        return Iterables.unmodifiableIterable(map.hexes());
    }

    @Override
    public Iterable<Hex> getVolcanos() {
        return Iterables.filter(map.hexes(), hex -> map.get(hex).getType() == FieldType.VOLCANO);
    }

    @Override
    public Village getVillage(Hex from) {
        return villages.get(from);
    }

    @Override
    public Iterable<Village> getVillages(PlayerColor color) {
        return villages.getAll(color);
    }

    Field doPutField(Hex hex, Field field) {
        Field fieldBefore = field == SEA
                ? map.remove(hex)
                : map.put(hex, field);
        return firstNonNull(fieldBefore, SEA);
    }

    @Override
    public void putField(Hex hex, Field field) {
        Field fieldBefore = doPutField(hex, field);
        if (fieldBefore.getBuilding().getType() != BuildingType.NONE) {
            villages.reset();
        }
    }

    public void putTile(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex leftHex = hex.getLeftNeighbor(orientation);
        Hex rightHex = hex.getRightNeighbor(orientation);

        int level = getField(hex).getLevel() + 1;

        putField(hex, Field.create(level, FieldType.VOLCANO, orientation));
        Field fieldBefore1 = doPutField(rightHex, Field.create(level, tile.getRight(), orientation.rightRotation()));
        Field fieldBefore2 = doPutField(leftHex, Field.create(level, tile.getLeft(), orientation.leftRotation()));
        if (fieldBefore1.getBuilding().getType() != BuildingType.NONE
                || fieldBefore2.getBuilding().getType() != BuildingType.NONE) {
            villages.reset();
        }
    }

    @Override
    public void putBuilding(Hex hex, FieldBuilding building) {
        if (map.contains(hex)) {
            Field fieldBefore = map.get(hex);
            map.put(hex, fieldBefore.withBuilding(building));
            villages.update(hex);
        }
    }

    @Override
    public Island copy() {
        return new IslandImpl(this);
    }
}
