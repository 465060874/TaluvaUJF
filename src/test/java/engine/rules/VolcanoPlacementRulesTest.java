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


public class VolcanoPlacementRulesTest {

    private static final VolcanoTile TILE = new VolcanoTile(FieldType.JUNGLE, FieldType.JUNGLE);

    private void assertValid(Island island, Hex hex, Orientation orientation) {
        Problems<PlacementProblem> actual = VolcanoPlacementRules.validate(island, TILE, hex, orientation);
        assertTrue("TileAction on volcano at " + hex
                        + " with orientation " + orientation
                        + " expected to be valid, but has problems " + actual,
                actual.isValid());
    }

    private void assertProblems(Island island, Hex hex, Orientation orientation,
                                PlacementProblem first, PlacementProblem... others) {
        Problems<PlacementProblem> expected = Problems.of(first, others);
        Problems<PlacementProblem> actual = VolcanoPlacementRules.validate(island, TILE, hex, orientation);
        assertEquals("TileAction on volcano at " + hex
                        + " with orientation " + orientation
                        + " expected to have problems " + expected
                        + ", but has " + actual,
                expected, actual);
    }

    @Test
    public void testOrientations() {
        URL rsc = VolcanoPlacementRulesTest.class.getResource("VolcanoPlacementRulesTest.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(0, 2);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.SAME_VOLCANO_ORIENTATION);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -2);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.SAME_VOLCANO_ORIENTATION);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.SOUTH);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ON_SAME_LEVEL);

        hex = Hex.at(2, 2);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.SOUTH_EAST);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.SAME_VOLCANO_ORIENTATION);

        hex = Hex.at(1, 0);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ON_SAME_LEVEL);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.SAME_VOLCANO_ORIENTATION);
        assertValid(island, hex, Orientation.SOUTH_EAST);
        assertValid(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(5, -1);
        assertProblems(island, hex, Orientation.NORTH, PlacementProblem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.NORTH_WEST, PlacementProblem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.NOT_ON_VOLCANO);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.NOT_ON_VOLCANO);

        hex = Hex.at(1, -1);
        assertValid(island, hex, Orientation.NORTH);
        assertValid(island, hex, Orientation.NORTH_WEST);
        assertValid(island, hex, Orientation.SOUTH_WEST);
        assertValid(island, hex, Orientation.SOUTH);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.SAME_VOLCANO_ORIENTATION);
        assertValid(island, hex, Orientation.NORTH_EAST);
    }

    @Test
    public void testVillages() {
        URL rsc = VolcanoPlacementRulesTest.class.getResource("VolcanoPlacementRulesTest2.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(-1, 2);
        assertValid(island, hex, Orientation.NORTH);

        hex = Hex.at(1, -1);
        assertProblems(island, hex, Orientation.SOUTH, PlacementProblem.CANT_DESTROY_TOWER_OR_TEMPLE);

        hex = Hex.at(1, 0);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.CANT_DESTROY_VILLAGE);
        assertProblems(island, hex, Orientation.NORTH_EAST, PlacementProblem.CANT_DESTROY_TOWER_OR_TEMPLE);

        hex = Hex.at(2, -2);
        assertProblems(island, hex, Orientation.SOUTH_WEST, PlacementProblem.CANT_DESTROY_TOWER_OR_TEMPLE);

        hex = Hex.at(2, 2);
        assertProblems(island, hex, Orientation.SOUTH_EAST, PlacementProblem.CANT_DESTROY_VILLAGE);

        hex = Hex.at(5, 0);
        assertValid(island, hex, Orientation.SOUTH);
    }
}
