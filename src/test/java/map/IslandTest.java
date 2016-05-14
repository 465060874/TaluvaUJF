package map;


import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IslandTest {

    @Test
    public void testGetField() {
        Island island = new IslandImpl();
        /*
         *   L
         * V     S/J
         *    J/L(Red Hutx2)<- origin   V/V
         *       R(White Hut)
         *
         */

        /*
        Testing the tile (0,0)
        Orientation: South-East
        Buildings: 2 Red Huts
         */

        Field field1 = island.getField(Hex.at(0, 0));

        assertEquals(2, field1.getLevel());
        assertEquals(FieldType.LAKE, field1.getType());
        assertEquals(BuildingType.NONE, field1.getBuilding().getType());
        assertEquals(2, field1.getBuilding().getCount());
        assertEquals(PlayerColor.RED, field1.getBuilding().getColor());
        assertEquals(BuildingType.HUT, field1.getBuilding().getType());

        /*
        Testing the tile (0,-1)
        Orientation: South-West
        Buildings: None
         */

        Field field2 = island.getField(Hex.at(0, -1));
        assertEquals(1, field2.getLevel());
        assertEquals(FieldType.VOLCANO, field2.getType());
        assertEquals(Orientation.SOUTH_WEST, field2.getOrientation());
        assertEquals(BuildingType.NONE, field1.getBuilding().getType());

        /*
        Testing the tile (1,0)
        Orientation: South-East
        Buildings: None
         */

        Field field3 = island.getField(Hex.at(1, 0));
        assertEquals(2, field3.getLevel());
        assertEquals(FieldType.VOLCANO, field3.getType());
        assertEquals(Orientation.NORTH_EAST, field3.getOrientation());
        assertEquals(BuildingType.NONE, field3.getBuilding().getType());

        /*
        Testing the tile (0,1)
        Orientation: North-East
        Buildings: 1 White Hut
         */

        Field field4 = island.getField(Hex.at(0, 1));
        assertEquals(1, field4.getLevel());
        assertEquals(FieldType.ROCK, field4.getType());
        assertEquals(Orientation.NORTH_EAST, field4.getOrientation());
        assertEquals(1, field4.getBuilding().getCount());
        assertEquals(PlayerColor.WHITE, field4.getBuilding().getColor());
        assertEquals(BuildingType.HUT, field4.getBuilding().getType());

    }

    @Test
    public void testGetCoast() {
        /*
         * Spaces:
         * (0,-2)
         * (0,-3)
         * (-1,-2)
         * (-1,-3)
         * (-2,-2)
         *
         * Coast:
         *
         * (0,3)
         * (0,-8)
         * (1,-8)
         * (1,-7)
         * (1,-6)
         * (1,-5)
         * (2,-5)
         * (3,-5)
         * (3,-4)
         * (3,-3)
         * (3,-2)
         * (4,-2)
         * (4,-1)
         * (3, 0)
         * (2, 1)
         * (1, 2)
         * (0,3)
         * (-1,4)
         * (-2,5)
         * (-3,5)
         * (-3,4)
         * (-3,3)
         * (-3,2)
         * (-2, 1)
         *
         */

        Island island = new IslandImpl();
        Set<Hex> actual = new HashSet<>();
        for (Hex hex : island.getCoast()) {
            if (!actual.add(hex))
                fail("Duplicated elements in getCoast(): " + hex);
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
