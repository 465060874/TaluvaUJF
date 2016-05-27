package engine;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.io.Resources;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.ExpandVillageAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import engine.rules.SeaTileRules;
import engine.rules.VolcanoTileRules;
import map.*;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class EngineActionTest {

    public static final int LIMIT = 10;

    private Set<SeaTileAction> getSeaTileActionsUnique(Engine engine) {
        Set<SeaTileAction> actual = new HashSet<>();
        // On vérifie l'unicité, on ne souhaite pas que l'iterable renvoyé par
        // contienne plusieurs fois le même élément
        engine.getSeaTileActions().stream().filter(e -> !actual.add(e))
                .forEach(action -> fail("Duplicated elements " + action));
        return actual;
    }

    private Set<VolcanoTileAction> getVolcanoTileActionsUnique(Engine engine) {
        Set<VolcanoTileAction> actual = new HashSet<>();
        engine.getVolcanoTileActions().stream().filter(e -> !actual.add(e))
                .forEach(action -> fail("Duplicated elements " + action));
        return actual;
    }

    private Set<Hex> getPlaceBuildingActionsUnique(Engine engine) {
        Set<Hex> actual = new HashSet<>();
        engine.getPlaceBuildingActions().stream().filter(e -> !actual.add(e.getHex()))
                .forEach(action -> fail("Duplicated elements " + action));
        return actual;
    }

    private Set<ExpandVillageAction> getExpandVillageActionsUnique(Engine engine) {
        Set<ExpandVillageAction> actual = new HashSet<>();
        engine.getExpandVillageActions().stream().filter(e -> !actual.add(e))
                .forEach(action -> fail("Duplicated elements " + action));
        return actual;
    }

    @Test
    public void testSeaTileActions() {
        URL rsc = EngineActionTest.class.getResource("EngineTest1.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.RED, e -> PlayerHandler.dummy())
                .player(PlayerColor.WHITE, e -> PlayerHandler.dummy())
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);

        VolcanoTile tile = engine.getVolcanoTileStack().current();

        Set<SeaTileAction> actual = getSeaTileActionsUnique(engine);


        ImmutableSet.Builder<SeaTileAction> builder = ImmutableSet.builder();
        for (int i = -LIMIT; i < LIMIT; i++) {
            for (int j = -LIMIT; j < LIMIT; j++) {
                Hex hex = Hex.at(i, j);
                for (Orientation orientation : Orientation.values()) {
                    if (!SeaTileRules.validate(island, tile, hex, orientation).isValid()) {
                        continue;
                    }

                    builder.add(new SeaTileAction(tile, hex, orientation));
                }
            }
        }

        ImmutableSet<SeaTileAction> expected = builder.build();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testVolcanoTileActions() {
        //TODO with Buildings
        //TODO with Levels
        //TODO with new tile
        URL rsc = EngineActionTest.class.getResource("EngineTest2.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.RED, e -> PlayerHandler.dummy())
                .player(PlayerColor.WHITE, e -> PlayerHandler.dummy())
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);

        VolcanoTile tile = engine.getVolcanoTileStack().current();

        Set<VolcanoTileAction> actual = getVolcanoTileActionsUnique(engine);

        ImmutableSet.Builder<VolcanoTileAction> builder = ImmutableSet.builder();
        for (int i = -LIMIT; i < LIMIT; i++) {
            for (int j = -LIMIT; j < LIMIT; j++) {
                Hex hex = Hex.at(i, j);
                for (Orientation orientation : Orientation.values()) {
                    if (!VolcanoTileRules.validate(island, tile, hex, orientation).isValid()) {
                        continue;
                    }

                    builder.add(new VolcanoTileAction(tile, hex, orientation));
                }
            }
        }
        ImmutableSet<VolcanoTileAction> expected = builder.build();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void updatePlaceBuildingTest() {
        //TODO with Buildings
        //TODO with Levels
        //TODO with new tile
        URL rsc = EngineActionTest.class.getResource("EngineTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.RED, e -> PlayerHandler.dummy())
                .player(PlayerColor.WHITE, e -> PlayerHandler.dummy())
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);

        ImmutableSet.Builder<Hex> builder = ImmutableSet.builder();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (field.getBuilding().getType() == BuildingType.NONE && field.getLevel() == 1 && field.getType() != FieldType.VOLCANO) {
                builder.add(hex);
            }
        }

        ImmutableSet<Hex> expected = builder.build();
        Set<Hex> actual = getPlaceBuildingActionsUnique(engine);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void updateExpandVillage_UniqueFieldTypeAndPlayerColor() {
        URL rsc = EngineActionTest.class.getResource("EngineTest4.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.RED, e -> PlayerHandler.dummy())
                .player(PlayerColor.WHITE, e -> PlayerHandler.dummy())
                .seed(121216213L)
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        // Vérification de l'état du jeu
        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.RED);

        // Les villages de l'engine correspondent à ceux de la map
        ImmutableSet.Builder<Hex> builder1 = ImmutableSet.builder();
        builder1.add(Hex.at(-2, 1));
        builder1.add(Hex.at(-2, 0));
        builder1.add(Hex.at(-1, -2));
        builder1.add(Hex.at(-2, -1));
        ImmutableSet<Hex> hexOfVillage1 = builder1.build();

        ImmutableSet.Builder<Hex> builder2 = ImmutableSet.builder();
        builder2.add(Hex.at(3, -2));
        ImmutableSet<Hex> hexOfVillage2 = builder2.build();

        ImmutableSet.Builder<Hex> builder3 = ImmutableSet.builder();
        builder3.add(Hex.at(2, -5));
        builder3.add(Hex.at(3, -5));
        ImmutableSet<Hex> hexOfVillage3 = builder3.build();

        ImmutableSet.Builder<Hex> builder4 = ImmutableSet.builder();
        builder4.add(Hex.at(4, -1));
        ImmutableSet<Hex> hexOfVillage4 = builder4.build();

        ImmutableSet.Builder<Hex> builder5 = ImmutableSet.builder();
        builder5.add(Hex.at(1, -3));
        ImmutableSet<Hex> hexOfVillage5 = builder5.build();

        ImmutableSet.Builder<Hex> builder6 = ImmutableSet.builder();
        builder6.add(Hex.at(1, 3));
        ImmutableSet<Hex> hexOfVillage6 = builder6.build();

        for (Hex hex : hexOfVillage1) {
            final Set<Hex> hexOfVillage1Found = engine.getIsland().getVillage(hex).getHexes();
            Assert.assertEquals(hexOfVillage1, hexOfVillage1Found);
        }
        for (Hex hex : hexOfVillage2) {
            final Set<Hex> hexOfVillage2Found = engine.getIsland().getVillage(hex).getHexes();
            Assert.assertEquals(hexOfVillage2, hexOfVillage2Found);
        }
        for (Hex hex : hexOfVillage3) {
            final Set<Hex> hexOfVillage3Found = engine.getIsland().getVillage(hex).getHexes();
            Assert.assertEquals(hexOfVillage3, hexOfVillage3Found);
        }
        for (Hex hex : hexOfVillage4) {
            final Set<Hex> hexOfVillage4Found = engine.getIsland().getVillage(hex).getHexes();
            Assert.assertEquals(hexOfVillage4, hexOfVillage4Found);
        }
        for (Hex hex : hexOfVillage5) {
            final Set<Hex> hexOfVillage5Found = engine.getIsland().getVillage(hex).getHexes();
            Assert.assertEquals(hexOfVillage5, hexOfVillage5Found);
        }
        for (Hex hex : hexOfVillage6) {
            final Set<Hex> hexOfVillage6Found = engine.getIsland().getVillage(hex).getHexes();
            Assert.assertEquals(hexOfVillage6, hexOfVillage6Found);
        }

        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesExpected = ImmutableSetMultimap.builder();
        builderVillagesExpected.put(island.getVillage(Hex.at(1, -3)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(-2, 1)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(3, -2)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(1, 3)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(4, -1)), FieldType.JUNGLE);
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeExpected = builderVillagesExpected.build();

        Set<ExpandVillageAction> actualAction = getExpandVillageActionsUnique(engine);
        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesActual = ImmutableSetMultimap.builder();
        for (ExpandVillageAction expandVillageAction : actualAction) {
            builderVillagesActual.put(expandVillageAction.getVillage(island), expandVillageAction.getFieldType());
        }
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeActual = builderVillagesActual.build();

        Assert.assertEquals(villagesFieldTypeExpected, villagesFieldTypeActual);
    }

}
