package map;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class HexTest {

    private static final int ORIGIN = 50;

    /*
    Neighbors           Orientations
    _....../NE\......_  ....N.......NE....
     \__...\__/...__/   .....\....../.....
    _/NO\__/02\__/ E\_  ......\..../......
    .\__/01\__/12\__/.  .......\__/.......
    ....\__/11\__/....  NW_____/  \_____SE
    ..__/10\__/21\__..  .......\__/.......
    _/O \__/20\__/SE\_  ......./..\.......
     \__/..\__/..\__/   ....../....\......
    _/...../  \.....\_  .... /......\.....
    .......\SW/.......  ...SW....... S....
    */

    @Test
    public void testGetNeighbor() {
        Hex origin = Hex.at(ORIGIN,ORIGIN);

        Hex hexWest = origin.getNeighbor(Neighbor.WEST);
        Hex hexNorthWest = origin.getNeighbor(Neighbor.NORTH_WEST);
        Hex hexNorthEast = origin.getNeighbor(Neighbor.NORTH_EAST);
        Hex hexEast = origin.getNeighbor(Neighbor.EAST);
        Hex hexSouthEast = origin.getNeighbor(Neighbor.SOUTH_EAST);
        Hex hexSouthWest = origin.getNeighbor(Neighbor.SOUTH_WEST);

        assertEquals(Hex.at(ORIGIN, ORIGIN -1),      hexWest);
        assertEquals(Hex.at(ORIGIN -1, ORIGIN),      hexNorthWest);
        assertEquals(Hex.at(ORIGIN -1, ORIGIN + 1),  hexNorthEast);
        assertEquals(Hex.at(ORIGIN, ORIGIN + 1),     hexEast);
        assertEquals(Hex.at(ORIGIN + 1, ORIGIN),     hexSouthEast);
        assertEquals(Hex.at(ORIGIN + 1, ORIGIN - 1), hexSouthWest);
    }

    @Test
    public void testGetNeighbors() {
        Hex origin = Hex.at(ORIGIN,ORIGIN);
        Set<Hex> expected = ImmutableSet.of(
                Hex.at(ORIGIN, ORIGIN -1),
                Hex.at(ORIGIN -1, ORIGIN),
                Hex.at(ORIGIN -1, ORIGIN + 1),
                Hex.at(ORIGIN, ORIGIN + 1),
                Hex.at(ORIGIN + 1, ORIGIN),
                Hex.at(ORIGIN + 1, ORIGIN - 1));

        ImmutableSet<Hex> actual = ImmutableSet.copyOf(origin.getNeighborhood());
        assertEquals(expected, actual);
    }

    @Test
    public void testGetLeftNeighbor() {
        Hex origin = Hex.at(ORIGIN,ORIGIN);

        Hex hexNorth = origin.getLeftNeighbor(Orientation.NORTH);
        Hex hexNorthWest = origin.getLeftNeighbor(Orientation.NORTH_WEST);
        Hex hexNorthEast = origin.getLeftNeighbor(Orientation.NORTH_EAST);
        Hex hexSouth = origin.getLeftNeighbor(Orientation.SOUTH);
        Hex hexSouthWest = origin.getLeftNeighbor(Orientation.SOUTH_WEST);
        Hex hexSouthEast = origin.getLeftNeighbor(Orientation.SOUTH_EAST);

        assertEquals(Hex.at(ORIGIN + 1, ORIGIN - 1), hexNorth);
        assertEquals(Hex.at(ORIGIN + 1, ORIGIN),     hexNorthWest);
        assertEquals(Hex.at(ORIGIN,     ORIGIN - 1), hexNorthEast);
        assertEquals(Hex.at(ORIGIN - 1, ORIGIN + 1), hexSouth);
        assertEquals(Hex.at(ORIGIN,     ORIGIN + 1), hexSouthWest);
        assertEquals(Hex.at(ORIGIN - 1, ORIGIN),     hexSouthEast);
    }

    @Test
    public void testGetRightNeighbor() {
        Hex origin = Hex.at(ORIGIN,ORIGIN);

        Hex hexNorth = origin.getRightNeighbor(Orientation.NORTH);
        Hex hexNorthWest = origin.getRightNeighbor(Orientation.NORTH_WEST);
        Hex hexNorthEast = origin.getRightNeighbor(Orientation.NORTH_EAST);
        Hex hexSouth = origin.getRightNeighbor(Orientation.SOUTH);
        Hex hexSouthWest = origin.getRightNeighbor(Orientation.SOUTH_WEST);
        Hex hexSouthEast = origin.getRightNeighbor(Orientation.SOUTH_EAST);

        assertEquals(Hex.at(ORIGIN + 1, ORIGIN),     hexNorth);
        assertEquals(Hex.at(ORIGIN,     ORIGIN + 1), hexNorthWest);
        assertEquals(Hex.at(ORIGIN + 1, ORIGIN - 1), hexNorthEast);
        assertEquals(Hex.at(ORIGIN - 1, ORIGIN),     hexSouth);
        assertEquals(Hex.at(ORIGIN - 1, ORIGIN + 1), hexSouthWest);
        assertEquals(Hex.at(ORIGIN,     ORIGIN - 1), hexSouthEast);
    }
}
