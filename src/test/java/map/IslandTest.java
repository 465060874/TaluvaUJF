package map;


import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static map.Hex.at;
import static org.junit.Assert.*;

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
        Iterable<Hex> coast = island.getCoast();
        Set<Hex> actual = new HashSet<>();

        // On vérifie l'unicité, on ne souhaite pas que l'iterable renvoyé par
        // getCoast contienne plusieurs fois le même élément
        for (Hex hex : coast) {
            if (!actual.add(hex)) {
                fail("Duplicated elements in getCoast(): " + hex);
            }
        }

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

        assertEquals(expected.size(), actual.size());
        for (Hex hex : expected) {
            assertTrue("Hex " + hex + " is part of the coast",
                    actual.contains(hex));
        }
    }

    @Test
    public void testGetVillages() {
        /*
        *
        * Empty Fields:
        * (1,-2) -> S
        * (3,-1) -> S
        * (2,0)  -> V
        * (0,2)  -> R
        * (-2,2) -> V
        * (-2,3) -> V
        */

    }

}
