package engine.rules;

import com.google.common.io.Resources;
import map.Hex;
import map.Island;
import map.IslandIO;
import map.Orientation;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class VolcanoTileRulesTest {

    private void assertValid(Island island, Hex hex, Orientation orientation) {
        Problem actual = VolcanoTileRules.validate(island, hex, orientation);
        assertTrue("TileAction on volcano at " + hex
                        + " with orientation " + orientation
                        + " expected to be valid, but has problems " + actual,
                actual.isValid());
    }

    private void assertProblems(Island island, Hex hex, Orientation orientation, Problem expected) {
        Problem actual = VolcanoTileRules.validate(island, hex, orientation);
        assertEquals("TileAction on volcano at " + hex
                        + " with orientation " + orientation
                        + " expected to have problems " + expected
                        + ", but has " + actual,
                expected, actual);
    }

    @Test
    public void testOrientations() {
        URL rsc = VolcanoTileRulesTest.class.getResource("VolcanoTileRulesTest.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(0, 2);
        assertProblems(island, hex, Orientation.NORTH, Problem.SAME_VOLCANO_ORIENTATION);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -2);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.SAME_VOLCANO_ORIENTATION);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.SOUTH);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ON_SAME_LEVEL);

        hex = Hex.at(2, 2);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.SOUTH_EAST);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.SAME_VOLCANO_ORIENTATION);

        hex = Hex.at(1, 0);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH, Problem.SAME_VOLCANO_ORIENTATION);
        assertValid(island, hex, Orientation.SOUTH_EAST);
        assertValid(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(5, -1);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ON_VOLCANO);

        hex = Hex.at(1, -1);
        assertValid(island, hex, Orientation.NORTH);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertValid(island, hex, Orientation.SOUTH);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.SAME_VOLCANO_ORIENTATION);
        assertValid(island, hex, Orientation.NORTH_EAST);
    }

    @Test
    public void testVillages() {
        URL rsc = VolcanoTileRulesTest.class.getResource("VolcanoTileRulesTest2.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(-1, 2);
        assertValid(island, hex, Orientation.NORTH);

        hex = Hex.at(1, -1);
        assertProblems(island, hex, Orientation.SOUTH, Problem.CANT_DESTROY_TOWER_OR_TEMPLE);

        hex = Hex.at(1, 0);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.CANT_DESTROY_VILLAGE);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.CANT_DESTROY_TOWER_OR_TEMPLE);

        hex = Hex.at(2, -2);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.CANT_DESTROY_TOWER_OR_TEMPLE);

        hex = Hex.at(2, 2);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.CANT_DESTROY_VILLAGE);

        hex = Hex.at(5, 0);
        assertValid(island, hex, Orientation.SOUTH);
    }
}
