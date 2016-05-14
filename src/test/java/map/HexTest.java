package map;

import org.junit.Assert;
import org.junit.Test;

public class HexTest {

    @Test
    public void testGetNeighbor() {
        Hex hex = Hex.at(10,10);
        Hex hexWestTrue = Hex.at(10,9);
        Hex neighborWest = hex.getNeighbor(Neighbor.WEST);
        Assert.assertEquals(hexWestTrue, neighborWest);
    }
}
