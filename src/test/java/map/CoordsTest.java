package map;

import org.junit.Assert;
import org.junit.Test;

public class CoordsTest {

    @Test
    public void getNeighborTest() {

        Coords coords = Coords.of(10,10);
        Coords coordsWestTrue = Coords.of(10,9);
        Coords neighborWest = coords.getNeighbor(Direction.WEST);
        Assert.assertEquals(coordsWestTrue, neighborWest);



    }
}
