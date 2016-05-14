package map;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class HexTest {
    private static int ORIGIN = 100;

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
}
