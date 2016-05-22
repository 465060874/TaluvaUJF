package engine.rules;

import com.google.common.io.Resources;
import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.Island;
import map.IslandIO;
import map.Orientation;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SeaPlacementRulesTest {

    private static final VolcanoTile TILE = new VolcanoTile(FieldType.JUNGLE, FieldType.JUNGLE);

    private void assertValid(Island island, Hex hex, Orientation orientation) {
        Problems<PlacementProblem> actual = SeaPlacementRules.validate(island, TILE, hex, orientation);
        assertTrue("TileAction on sea at " + hex
                        + " with orientation " + orientation
                        + " expected to be valid, but has problems " + actual,
                actual.isValid());
    }

    private void assertProblems(Island island, Hex hex, Orientation orientation,
                                PlacementProblem first, PlacementProblem... others) {
        Problems<PlacementProblem> expected = Problems.of(first, others);
        Problems<PlacementProblem> actual = SeaPlacementRules.validate(island, TILE, hex, orientation);
        assertEquals("TileAction on sea at " + hex
                        + " with orientation " + orientation
                        + " expected to have problems " + expected
                        + ", but has " + actual,
                expected, actual);
    }

    @Test
    public void testValidate() {
        URL rsc = SeaPlacementRulesTest.class.getResource("SeaPlacementRulesTest.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(0, -1);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);

        hex = Hex.at(0, 0);
        assertValid(island, hex, Orientation.NORTH);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);
        assertValid(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -3);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);

        hex = Hex.at(-1, 0);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);

        hex = Hex.at(4, -3);
        assertValid(island, hex, Orientation.NORTH);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ALL_ON_SEA);

        hex = Hex.at(-1, -4);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ADJACENT_TO_COAST);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ADJACENT_TO_COAST);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ADJACENT_TO_COAST);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ADJACENT_TO_COAST);

    }
}
