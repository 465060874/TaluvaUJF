package map;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class HexTest {
    private static final int ORIGIN = 100;

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
        Hex hexWestExpected = Hex.at(ORIGIN, ORIGIN -1);
        Hex hexNorthWestExpected = Hex.at(ORIGIN -1, ORIGIN);
        Hex hexNorthEastExpected = Hex.at(ORIGIN -1, ORIGIN + 1);
        Hex hexEastExpected = Hex.at(ORIGIN, ORIGIN + 1);
        Hex hexSouthEastExpected = Hex.at(ORIGIN + 1, ORIGIN);
        Hex hexSouthWestExpected = Hex.at(ORIGIN + 1, ORIGIN - 1);

        Hex hexWest = origin.getNeighbor(Neighbor.WEST);
        Hex hexNorthWest = origin.getNeighbor(Neighbor.NORTH_WEST);
        Hex hexNorthEast = origin.getNeighbor(Neighbor.NORTH_EAST);
        Hex hexEast = origin.getNeighbor(Neighbor.EAST);
        Hex hexSouthEast = origin.getNeighbor(Neighbor.SOUTH_EAST);
        Hex hexSouthWest = origin.getNeighbor(Neighbor.SOUTH_WEST);

        Assert.assertEquals(hexWestExpected, hexWest);
        Assert.assertEquals(hexNorthWestExpected, hexNorthWest);
        Assert.assertEquals(hexNorthEastExpected, hexNorthEast);
        Assert.assertEquals(hexEastExpected, hexEast);
        Assert.assertEquals(hexSouthEastExpected, hexSouthEast);
        Assert.assertEquals(hexSouthWestExpected, hexSouthWest);

    }

    @Test
    public void testGetNeighbors() {

        Hex origin = Hex.at(ORIGIN,ORIGIN);
        Hex hexWestExpected = Hex.at(ORIGIN, ORIGIN -1);
        Hex hexNorthWestExpected = Hex.at(ORIGIN -1, ORIGIN);
        Hex hexNorthEastExpected = Hex.at(ORIGIN -1, ORIGIN + 1);
        Hex hexEastExpected = Hex.at(ORIGIN, ORIGIN + 1);
        Hex hexSouthEastExpected = Hex.at(ORIGIN + 1, ORIGIN);
        Hex hexSouthWestExpected = Hex.at(ORIGIN + 1, ORIGIN - 1);

        ArrayList<Hex> neighbors = new ArrayList<>();
        neighbors.add(hexWestExpected);
        neighbors.add(hexNorthWestExpected);
        neighbors.add(hexNorthEastExpected);
        neighbors.add(hexEastExpected);
        neighbors.add(hexSouthEastExpected);
        neighbors.add(hexSouthWestExpected);

        Collections.sort(neighbors);

        Iterable<Hex> neighborsExpectedIt = origin.getNeighborhood();
        ArrayList<Hex> neighborsExpected = new ArrayList<>();
        for (Hex hex : neighborsExpectedIt) {
            neighborsExpected.add(hex);
        }
        Assert.assertEquals(neighborsExpected, neighbors);
    }

    @Test
    public void getLeftNeighborTest() {
        Hex origin = Hex.at(ORIGIN,ORIGIN);
        final Orientation[] orientations = Orientation.values();

        Hex hexNorth = origin.getLeftNeighbor(orientations[0]);
        Hex hexNorthWest = origin.getLeftNeighbor(orientations[1]);
        Hex hexNorthEast = origin.getLeftNeighbor(orientations[2]);
        Hex hexSouth = origin.getLeftNeighbor(orientations[3]);
        Hex hexSouthWest = origin.getLeftNeighbor(orientations[4]);
        Hex hexSouthEast = origin.getLeftNeighbor(orientations[5]);

        Assert.assertEquals(hexNorth, Hex.at(ORIGIN + 1, ORIGIN - 1));
        Assert.assertEquals(hexNorthWest, Hex.at(ORIGIN + 1, ORIGIN));
        Assert.assertEquals(hexNorthEast, Hex.at(ORIGIN, ORIGIN - 1));
        Assert.assertEquals(hexSouth, Hex.at(ORIGIN - 1, ORIGIN + 1));
        Assert.assertEquals(hexSouthWest, Hex.at(ORIGIN, ORIGIN + 1));
        Assert.assertEquals(hexSouthEast, Hex.at(ORIGIN - 1, ORIGIN));
    }

    @Test
    public void getRightNeighborTest() {
        Hex origin = Hex.at(ORIGIN,ORIGIN);
        final Orientation[] orientations = Orientation.values();

        Hex hexNorth = origin.getRightNeighbor(orientations[0]);
        Hex hexNorthWest = origin.getRightNeighbor(orientations[1]);
        Hex hexNorthEast = origin.getRightNeighbor(orientations[2]);
        Hex hexSouth = origin.getRightNeighbor(orientations[3]);
        Hex hexSouthWest = origin.getRightNeighbor(orientations[4]);
        Hex hexSouthEast = origin.getRightNeighbor(orientations[5]);

        Assert.assertEquals(hexNorth, Hex.at(ORIGIN + 1, ORIGIN));
        Assert.assertEquals(hexNorthWest, Hex.at(ORIGIN, ORIGIN + 1));
        Assert.assertEquals(hexNorthEast, Hex.at(ORIGIN + 1, ORIGIN - 1));
        Assert.assertEquals(hexSouth, Hex.at(ORIGIN - 1, ORIGIN));
        Assert.assertEquals(hexSouthWest, Hex.at(ORIGIN - 1, ORIGIN + 1));
        Assert.assertEquals(hexSouthEast, Hex.at(ORIGIN, ORIGIN - 1));
    }
}
