package map;


import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import engine.Player;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

public class MapTest {

    @Test
    public void getFieldTest() {
        Map map = null;
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

        Field field1 = map.getField(Coords.of(0,0)).get();

        assertEquals(2, field1.getLevel());
        assertEquals(FieldType.LAKE, field1.getType());
        assertEquals(BuildingType.NONE, field1.getBuilding().getBuildingType());
        assertEquals(2, field1.getBuilding().getBuildingCount());
        assertEquals(PlayerColor.RED, field1.getBuilding().getBuildingColor());
        assertEquals(BuildingType.HUT, field1.getBuilding().getBuildingType());

        /*
        Testing the tile (0,-1)
        Orientation: South-West
        Buildings: None
         */

        Field field2 = map.getField(Coords.of(0,-1)).get();
        assertEquals(1, field2.getLevel());
        assertEquals(FieldType.VOLCANO, field2.getType());
        assertEquals(Orientation.SOUTH_WEST, field2.getOrientation());
        assertEquals(BuildingType.NONE, field1.getBuilding().getBuildingType());

        /*
        Testing the tile (1,0)
        Orientation: South-East
        Buildings: None
         */

        Field field3 = map.getField(Coords.of(1,0)).get();
        assertEquals(2, field3.getLevel());
        assertEquals(FieldType.VOLCANO, field3.getType());
        assertEquals(Orientation.NORTH_EAST, field3.getOrientation());
        assertEquals(BuildingType.NONE, field3.getBuilding().getBuildingType());

        /*
        Testing the tile (0,1)
        Orientation: North-East
        Buildings: 1 White Hut
         */

        Field field4 = map.getField(Coords.of(0,1)).get();
        assertEquals(1, field4.getLevel());
        assertEquals(FieldType.ROCK, field4.getType());
        assertEquals(Orientation.NORTH_EAST, field4.getOrientation());
        assertEquals(1, field4.getBuilding().getBuildingCount());
        assertEquals(PlayerColor.WHITE, field4.getBuilding().getBuildingColor());
        assertEquals(BuildingType.HUT, field4.getBuilding().getBuildingType());

    }

    @Test
    public void getEmptyFieldsTest(){
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

        Map map = null;
        Iterator<Coords> iterator = map.getSeaLevel();
        Set<Coords> actual = new HashSet<>();

        while(iterator.hasNext()){
            if (!actual.add(iterator.next()))
                fail("Duplicated elemenets in getEmptyFields()");
        }







    }

    @Test
    public void getVillagesTest(){
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
