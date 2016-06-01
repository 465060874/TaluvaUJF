package map;

import com.google.common.collect.Iterables;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;

import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static map.Field.SEA;

class IslandImpl implements Island {

    final HexMap<Field> map;
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
    public boolean isEmpty() {
        return map.size() == 0;
    }

    @Override
    public Field getField(Hex hex) {
        return map.getOrDefault(hex, SEA);
    }

    @Override
    public Iterable<Hex> getCoast() {
        return map.hexes().stream()
                .flatMap(h -> h.getNeighborhood().stream())
                .filter(h -> !map.contains(h))
                .collect(Collectors.toSet());
    }

    @Override
    public Iterable<Hex> getFields() {
        return Iterables.unmodifiableIterable(map.hexes());
    }

    @Override
    public Iterable<Hex> getVolcanos() {
        return map.hexes().stream()
                .filter(hex -> map.get(hex).getType() == FieldType.VOLCANO)
                .collect(Collectors.toSet());
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
        if (fieldBefore.hasBuilding()) {
            villages.reset(hex);
        }
        else if (field.hasBuilding()) {
            villages.update(hex);
        }
    }

    public void putTile(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex leftHex = hex.getLeftNeighbor(orientation);
        Hex rightHex = hex.getRightNeighbor(orientation);

        int level = getField(hex).getLevel() + 1;

        doPutField(hex, Field.create(level, FieldType.VOLCANO, orientation));
        Field leftBefore = doPutField(leftHex, Field.create(level, tile.getLeft(), orientation.leftRotation()));
        Field rightBefore = doPutField(rightHex, Field.create(level, tile.getRight(), orientation.rightRotation()));

        if (leftBefore.hasBuilding() && rightBefore.hasBuilding()) {
            villages.reset(leftHex, rightHex);
        }
        else if (leftBefore.hasBuilding()) {
            villages.reset(leftHex);
        }
        else if (rightBefore.hasBuilding()) {
            villages.reset(rightHex);
        }
    }

    @Override
    public void putBuilding(Hex hex, Building building) {
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

    @Override
    public double getHashFactor() {
        return map.getHashFactor();
    }
}
