package map;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static map.Hex.at;
import static org.junit.Assert.assertEquals;
import static util.SetTest.assertNoDuplicatesAndCreateSet;

public class IslandTest {

    @Test
    public void testGetField() {
        URL rsc = IslandTest.class.getResource("IslandTest1.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Field field;

        field = island.getField(at(0, 0));
        assertEquals(2, field.getLevel());
        assertEquals(FieldType.LAKE, field.getType());
        assertEquals(PlayerColor.RED, field.getBuilding().getColor());
        assertEquals(BuildingType.HUT, field.getBuilding().getType());

        field = island.getField(at(-1, 0));
        assertEquals(1, field.getLevel());
        assertEquals(FieldType.VOLCANO, field.getType());
        assertEquals(Orientation.SOUTH_WEST, field.getOrientation());
        assertEquals(BuildingType.NONE, field.getBuilding().getType());

        field = island.getField(at(0, 1));
        assertEquals(2, field.getLevel());
        assertEquals(FieldType.VOLCANO, field.getType());
        assertEquals(Orientation.SOUTH_EAST, field.getOrientation());
        assertEquals(BuildingType.NONE, field.getBuilding().getType());

        field = island.getField(at(1, 0));
        assertEquals(1, field.getLevel());
        assertEquals(FieldType.ROCK, field.getType());
        assertEquals(Orientation.SOUTH, field.getOrientation());
        assertEquals(PlayerColor.WHITE, field.getBuilding().getColor());
        assertEquals(BuildingType.HUT, field.getBuilding().getType());

    }

    @Test
    public void testGetCoast() {
        URL rsc = IslandTest.class.getResource("IslandTest2.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Set<Hex> actual = assertNoDuplicatesAndCreateSet(island.getCoast());

        ImmutableSet<Hex> expected = ImmutableSet.of(
                Hex.at(-3, -2),
                Hex.at(-3, -1),
                Hex.at(-2, -1),
                Hex.at(-1, -1),
                Hex.at(0, -1),
                Hex.at(1, -2),
                Hex.at(1, -3),
                Hex.at(1, -4),
                Hex.at(0, -4),
                Hex.at(-1, -4),
                Hex.at(-2, -3)
        );

        assertEquals(expected, actual);
    }

    @Test
    public void testGetVolcanos() {
        URL rsc = IslandTest.class.getResource("IslandTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Set<Hex> actual = assertNoDuplicatesAndCreateSet(island.getVolcanos());

        ImmutableSet<Hex> expected = ImmutableSet.of(
                Hex.at(-1, -1),
                Hex.at(-1, 0),
                Hex.at(-2, 2),
                Hex.at(0, 2),
                Hex.at(3, -1),
                Hex.at(0, -2),
                Hex.at(2, -2),
                Hex.at(2, -4),
                Hex.at(4, -5)
        );

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetVillageWithoutBuilding() {
        URL rsc = IslandTest.class.getResource("IslandTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        island.getVillage(Hex.at(0, -2));
    }

    @Test
    public void testGetVillageWithOneHut() {
        URL rsc = IslandTest.class.getResource("IslandTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Village oneHutVillage = island.getVillage(Hex.at(3, -2));
        Set<Hex> actual = oneHutVillage.getHexes();

        ImmutableSet<Hex> expected = ImmutableSet.of(Hex.at(3, -2));

        assertEquals(expected, actual);
    }

    @Test
    public void testGetVillageWithMultipleBuildings() {
        URL rsc = IslandTest.class.getResource("IslandTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Village bigVillage = island.getVillage(Hex.at(0, 1));
        Set<Hex> actual = bigVillage.getHexes();

        ImmutableSet<Hex> expected = ImmutableSet.of(
                Hex.at(-2, 0),
                Hex.at(-2, 1),
                Hex.at(-1, 1),
                Hex.at(0, 1),
                Hex.at(1, 1),
                Hex.at(2, 0)
        );

        assertEquals(expected, actual);
    }

    @Test
    public void testGetVillages() {
        URL rsc = IslandTest.class.getResource("IslandTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Iterable<Village> redVillages = island.getVillages(PlayerColor.RED);
        Iterable<Village> yellowVillages = island.getVillages(PlayerColor.YELLOW);

        assertEquals(2, Iterables.size(redVillages));
        assertEquals(3, Iterables.size(yellowVillages));
    }
}
