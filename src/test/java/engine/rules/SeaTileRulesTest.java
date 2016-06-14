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


public class SeaTileRulesTest {

    private void assertValid(Island island, Hex hex, Orientation orientation) {
        Problem actual = SeaTileRules.validate(island, hex, orientation);
        assertTrue("TileAction on sea at " + hex
                        + " with orientation " + orientation
                        + " expected to be valid, but has problems " + actual,
                actual.isValid());
    }

    private void assertProblems(Island island, Hex hex, Orientation orientation, Problem expected) {
        Problem actual = SeaTileRules.validate(island, hex, orientation);
        assertEquals("TileAction on sea at " + hex
                        + " with orientation " + orientation
                        + " expected to have problems " + expected
                        + ", but has " + actual,
                expected, actual);
    }

    @Test
    public void testValidate() {
        URL rsc = SeaTileRulesTest.class.getResource("SeaTileRulesTest.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(0, -1);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ALL_ON_SEA);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ALL_ON_SEA);

        hex = Hex.at(0, 0);
        assertValid(island, hex, Orientation.NORTH);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ALL_ON_SEA);
        assertValid(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -3);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ALL_ON_SEA);

        hex = Hex.at(-1, 0);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ALL_ON_SEA);

        hex = Hex.at(4, -3);
        assertValid(island, hex, Orientation.NORTH);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH_WEST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ALL_ON_SEA);

        hex = Hex.at(-1, -4);
        assertProblems(island, hex, Orientation.NORTH, Problem.NOT_ADJACENT_TO_COAST);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH, Problem.NOT_ADJACENT_TO_COAST);
        assertProblems(island, hex, Orientation.SOUTH_EAST, Problem.NOT_ADJACENT_TO_COAST);
        assertProblems(island, hex, Orientation.NORTH_EAST, Problem.NOT_ADJACENT_TO_COAST);

    }
}
