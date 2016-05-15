package engine;

import com.google.common.io.Resources;
import data.FieldType;
import data.VolcanoTile;
import map.*;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class EngineImplTest {
    private static final FieldType FIELD = FieldType.JUNGLE;


    @Test
    public void isOnSameLevelRuleTest() {
        IslandImpl island = new IslandImpl();
        EngineImpl engine = new EngineImpl(island);
        Hex[] hexes = new Hex[]{Hex.at(0, 1), Hex.at(1, 1), Hex.at(1, 0)};

        int level = 0;
        assertTrue("Pas tous au même niveau " + prettyPrintHexes(island, hexes),
                engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2]));
        assertTrue("Pas tous au niveau " + level + " " + prettyPrintHexes(island, hexes),
                engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2], level));

        level ++;
        island.putTile(new VolcanoTile(FIELD, FIELD), hexes[0], Orientation.NORTH);
        assertTrue("Pas tous au même niveau " + prettyPrintHexes(island, hexes),
                engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2]));
        assertTrue("Pas tous au niveau " + level + " " + prettyPrintHexes(island, hexes),
                engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2], level));
    }

    private String prettyPrintHexes(IslandImpl island, Hex[] hexes) {
        return Arrays.toString(hexes)
                + " [ " +
                island.getField(hexes[0]).getLevel() + " " +
                island.getField(hexes[1]).getLevel() + " " +
                island.getField(hexes[2]).getLevel() + " ] ";
    }


    @Test
    public void canPlaceTileOnVolcanoWithoutBuildingsTest() {
        URL rsc = EngineImplTest.class.getResource("canPlaceTileOnVolcanoWithoutBuildings.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        EngineImpl engine = new EngineImpl(island);

        Hex[] volcanoHexes = new Hex[]{
                Hex.at(0, 2), Hex.at(2, -2),
                Hex.at(2, 2), Hex.at(1, 0),
                Hex.at(4, -1), Hex.at(1, -1)};
        VolcanoTile tile = new VolcanoTile(FIELD, FIELD);
        Orientation[] orientations = Orientation.values();

        // Test de pose sur un volcan de même orientation
        for (int i = 0; i < orientations.length; i++) {
            assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[i], orientations[i]));
        }

        // Toute les orientations sont valides
        for (int i = 0; i < orientations.length; i++) {
            if (i != orientations[5].ordinal()) {
                assertTrue("Echec de pose de tuile d'orientation " + Orientation.values()[i].toString() + " " + i,
                        engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[5], orientations[i]));
            }
        }

        // Test de pose sur vide
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[0], Orientation.SOUTH));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[0], Orientation.SOUTH_EAST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[0], Orientation.SOUTH_WEST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[0], Orientation.NORTH_WEST));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[0], Orientation.NORTH_EAST));

        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[1], Orientation.NORTH));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[1], Orientation.NORTH_EAST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[1], Orientation.SOUTH_EAST));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[1], Orientation.SOUTH));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[1], Orientation.SOUTH_WEST));

        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[2], Orientation.SOUTH));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[2], Orientation.NORTH_EAST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[2], Orientation.SOUTH_WEST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[2], Orientation.NORTH_WEST));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[2], Orientation.SOUTH_EAST));

        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[3], Orientation.NORTH));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[3], Orientation.NORTH_WEST));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[3], Orientation.SOUTH_WEST));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[3], Orientation.NORTH_EAST));
        assertTrue(engine.canPlaceTileOnVolcano(tile, volcanoHexes[3], Orientation.SOUTH_EAST));

        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[4], Orientation.SOUTH));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[4], Orientation.NORTH_EAST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[4], Orientation.NORTH));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[4], Orientation.SOUTH_EAST));
        assertFalse(engine.canPlaceTileOnVolcano(tile, volcanoHexes[4], Orientation.NORTH_WEST));
    }

    public void canPlaceTileOnSeaTest() {

    }

}
