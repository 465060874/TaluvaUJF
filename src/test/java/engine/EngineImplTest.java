package engine;

import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.IslandImpl;
import map.Orientation;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;


public class EngineImplTest {
    private static final FieldType FIELD = FieldType.JUNGLE;


    @Test
    public void isOnSameLevelRuleTest() {
        IslandImpl island = new IslandImpl();
        EngineImpl engine = new EngineImpl(island);
        Hex[] hexes = new Hex[]{Hex.at(0, 1), Hex.at(1, 1), Hex.at(1, 0)};

        int level = 0;
        Assert.assertTrue("Pas tous au même niveau " + prettyPrintHexes(island, hexes),
                engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2]));
        Assert.assertTrue("Pas tous au niveau " + level + " " + prettyPrintHexes(island, hexes), engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2], level));

        level ++;
        island.putTile(new VolcanoTile(FIELD, FIELD), hexes[0], Orientation.NORTH);
        Assert.assertTrue("Pas tous au même niveau " + prettyPrintHexes(island, hexes),
                engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2]));
        Assert.assertTrue("Pas tous au niveau " + level + " " + prettyPrintHexes(island, hexes), engine.isOnSameLevelRule(hexes[0], hexes[1], hexes[2], level));
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
        /*

        N......................E  N......................E
        ............__..........  ............__..........
        .........__/0 \__.......  .........__/04\__.......
        ......__/  \     \__....  ......__/03\__/14\__....
        ...__/     /   __/ 2\...  ...__/02\__/13\__/24\...
        ../  \__  3\__/     /...  ../01\__/12\__/23\__/...
        ..\    5\__/..\__   \...  ..\__/11\__/..\__/33\...
        ../   __/  \../  \__/...  ../10\__/21\../32\__/...
        ..\__/1    /..\     \...  ..\__/20\__/..\__/42\...
        .....\__   \../4  __/...  .....\__/31\../41\__/...
        ........\__/..\__/......  ........\__/..\__/......
        W......................S  W......................S

        */

        IslandImpl island = new IslandImpl();
        EngineImpl engine = new EngineImpl(island);

        Hex[] volcanoHexes = new Hex[]{
                Hex.at(0, 4), Hex.at(2, 0),
                Hex.at(2, 4), Hex.at(1, 2),
                Hex.at(4, 1), Hex.at(1, 1)};

        Orientation[] orientations = Orientation.values();

        for (int i = 0; i < orientations.length; i++) {
            island.putTile(new VolcanoTile(FIELD, FIELD), volcanoHexes[i], orientations[i]);
        }

        // Test de pose sur un volcan de même orientation
        for (int i = 0; i < orientations.length; i++) {
            Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[i], orientations[i]));
        }

        // Toute les orientations sont valides
        for (int i = 0; i < orientations.length; i++) {
            if (i != orientations[5].ordinal()) {
                Assert.assertTrue("Echec de pose de tuile d'orientation " + Orientation.values()[i].toString() + " " + i,
                        engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[5], orientations[i]));
            }
        }

        // Test de pose sur vide
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[0], Orientation.SOUTH));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[0], Orientation.SOUTH_EAST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[0], Orientation.SOUTH_WEST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[0], Orientation.NORTH_WEST));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[0], Orientation.NORTH_EAST));

        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[1], Orientation.NORTH));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[1], Orientation.NORTH_EAST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[1], Orientation.SOUTH_EAST));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[1], Orientation.SOUTH));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[1], Orientation.SOUTH_WEST));

        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[2], Orientation.SOUTH));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[2], Orientation.NORTH_EAST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[2], Orientation.SOUTH_WEST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[2], Orientation.NORTH_WEST));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[2], Orientation.SOUTH_EAST));

        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[3], Orientation.NORTH));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[3], Orientation.NORTH_WEST));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[3], Orientation.SOUTH_WEST));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[3], Orientation.NORTH_EAST));
        Assert.assertTrue(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[3], Orientation.SOUTH_EAST));

        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[4], Orientation.SOUTH));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[4], Orientation.NORTH_EAST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[4], Orientation.NORTH));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[4], Orientation.SOUTH_EAST));
        Assert.assertFalse(engine.canPlaceTileOnVolcano(new VolcanoTile(FIELD, FIELD), volcanoHexes[4], Orientation.NORTH_WEST));

    }

    public void canPlaceTileOnSeaTest() {

    }

}
