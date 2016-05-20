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


public class SeaPlacementRulesTest {

    private static final VolcanoTile TILE = new VolcanoTile(FieldType.JUNGLE, FieldType.JUNGLE);

    @Test
    public void testValidate() {
        URL rsc = SeaPlacementRulesTest.class.getResource("SeaPlacementRulesTest.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));

        Hex hex;

        hex = Hex.at(0, -1);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateTrue_(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(0, 0);
        assertValidateTrue_(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateTrue_(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -3);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(-1, 0);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(2, -3);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateFalse(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(4, -3);
        assertValidateTrue_(island, hex, Orientation.NORTH);
        assertValidateTrue_(island, hex, Orientation.NORTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

        hex = Hex.at(-1, -4);
        assertValidateFalse(island, hex, Orientation.NORTH);
        assertValidateTrue_(island, hex, Orientation.NORTH_WEST);
        assertValidateTrue_(island, hex, Orientation.SOUTH_WEST);
        assertValidateFalse(island, hex, Orientation.SOUTH);
        assertValidateFalse(island, hex, Orientation.SOUTH_EAST);
        assertValidateFalse(island, hex, Orientation.NORTH_EAST);

    }

    private void assertValidateFalse(Island island, Hex hex, Orientation orientation) {
        assertFalse("Placement on sea at " + hex
                        + " with orientation " + orientation
                        + " expected to not be valid, but was",
                SeaPlacementRules.validate(island, TILE, hex, orientation));
    }

    private void assertValidateTrue_(Island island, Hex hex, Orientation orientation) {
        assertTrue("Placement on sea at " + hex
                        + " with orientation " + orientation
                        + " expected to be valid, but was not",
                SeaPlacementRules.validate(island, TILE, hex, orientation));
    }
}