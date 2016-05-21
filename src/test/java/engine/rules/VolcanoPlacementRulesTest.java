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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class VolcanoPlacementRulesTest {

    private static final VolcanoTile TILE = new VolcanoTile(FieldType.JUNGLE, FieldType.JUNGLE);

    private void assertValidateFalse(Island island, Hex hex, Orientation orientation) {
        assertFalse("TileAction on volcano at " + hex
                        + " with orientation " + orientation
                        + " expected to not be valid, but was",
                VolcanoPlacementRules.validate(island, TILE, hex, orientation));
    }

    private void assertValidateTrue_(Island island, Hex hex, Orientation orientation) {
        assertTrue("TileAction on volcano at " + hex
                        + " with orientation " + orientation
                        + " expected to be valid, but was not",
                VolcanoPlacementRules.validate(island, TILE, hex, orientation));
    }

    @Test
    public void testOrientations() {
        URL rsc = VolcanoPlacementRulesTest.class.getResource("VolcanoPlacementRulesTest.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(0, 2);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateTrue_(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -2);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateTrue_(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateTrue_(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, 2);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateTrue_(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(1, 0);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateTrue_(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateTrue_(island, hex, Orientation.SOUTH_EAST);
        assertValidateTrue_(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(5, -1);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(1, -1);
        assertValidateTrue_(island, hex, Orientation.NORTH);
        assertValidateTrue_(island, hex, Orientation.NORTH_WEST);
        assertValidateTrue_(island, hex, Orientation.SOUTH_WEST);
        assertValidateTrue_(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateTrue_(island, hex, Orientation.NORTH_EAST);
    }

    @Test
    public void testVillages() {
        URL rsc = VolcanoPlacementRulesTest.class.getResource("VolcanoPlacementRulesTest2.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(1, 0);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
    }
}
