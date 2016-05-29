package engine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.io.Resources;
import data.FieldType;
import data.PlayerColor;
import engine.action.ExpandVillageAction;
import map.Hex;
import map.Island;
import map.IslandIO;
import map.Village;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class UpdateExpandVillage {

    private Set<ExpandVillageAction> getExpandVillageActionsUnique(Engine engine) {
        Set<ExpandVillageAction> actual = new HashSet<>();
        engine.getExpandVillageActions().stream().filter(e -> !actual.add(e))
                .forEach(action -> fail("Duplicated elements " + action));
        return actual;
    }

    @Test
    public void updateExpandVillage_UniqueFieldTypeAndPlayerColor() {
        URL rsc = EngineActionTest.class.getResource("EngineTest4.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.withPredefinedPlayers(Gamemode.AllVsAll,
                ImmutableMap.of(
                        PlayerColor.RED, e -> PlayerHandler.dummy(),
                        PlayerColor.WHITE, e -> PlayerHandler.dummy()))
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

    @Test
    public void updateExpandVillage_UniqueFieldType_Red() {
        URL rsc = EngineActionTest.class.getResource("EngineTest5.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine =  EngineBuilder.withPredefinedPlayers(Gamemode.AllVsAll,
                ImmutableMap.of(
                        PlayerColor.RED, e -> PlayerHandler.dummy(),
                        PlayerColor.WHITE, e -> PlayerHandler.dummy()))
                .seed(121216213L)
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.RED);

        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesExpected = ImmutableSetMultimap.builder();
        builderVillagesExpected.put(island.getVillage(Hex.at(-2, 1)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(2, 0)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(4, -1)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(1, 3)), FieldType.JUNGLE);
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeExpected = builderVillagesExpected.build();

        Set<ExpandVillageAction> actualAction = getExpandVillageActionsUnique(engine);
        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesActual = ImmutableSetMultimap.builder();
        for (ExpandVillageAction expandVillageAction : actualAction) {
            builderVillagesActual.put(expandVillageAction.getVillage(island), expandVillageAction.getFieldType());
        }
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeActual = builderVillagesActual.build();

        Assert.assertEquals(villagesFieldTypeExpected, villagesFieldTypeActual);
    }

    @Test
    public void updateExpandVillage_UniqueFieldType_White() {
        URL rsc = EngineActionTest.class.getResource("EngineTest5.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.withPredefinedPlayers(Gamemode.AllVsAll,
                ImmutableMap.of(
                        PlayerColor.WHITE, e -> PlayerHandler.dummy(),
                        PlayerColor.RED, e -> PlayerHandler.dummy()))
                .seed(126266215L)
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.WHITE);

        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesExpected = ImmutableSetMultimap.builder();
        builderVillagesExpected.put(island.getVillage(Hex.at(-1, -2)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(1, -3)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(2, 1)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(4, 0)), FieldType.JUNGLE);
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeExpected = builderVillagesExpected.build();

        Set<ExpandVillageAction> actualAction = getExpandVillageActionsUnique(engine);
        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesActual = ImmutableSetMultimap.builder();
        for (ExpandVillageAction expandVillageAction : actualAction) {
            builderVillagesActual.put(expandVillageAction.getVillage(island), expandVillageAction.getFieldType());
        }
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeActual = builderVillagesActual.build();

        Assert.assertEquals(villagesFieldTypeExpected, villagesFieldTypeActual);
    }

    @Test
    public void updateExpandVillage_UniqueFieldType_Brown() {
        URL rsc = EngineActionTest.class.getResource("EngineTest5.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.BROWN, e -> PlayerHandler.dummy())
                .player(PlayerColor.RED, e -> PlayerHandler.dummy())
                .player(PlayerColor.WHITE, e -> PlayerHandler.dummy())
                .seed(123276805L)
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.BROWN);

        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesExpected = ImmutableSetMultimap.builder();
        builderVillagesExpected.put(island.getVillage(Hex.at(4, -4)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(-2, 0)), FieldType.JUNGLE);
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeExpected = builderVillagesExpected.build();

        Set<ExpandVillageAction> actualAction = getExpandVillageActionsUnique(engine);
        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesActual = ImmutableSetMultimap.builder();
        for (ExpandVillageAction expandVillageAction : actualAction) {
            builderVillagesActual.put(expandVillageAction.getVillage(island), expandVillageAction.getFieldType());
        }
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeActual = builderVillagesActual.build();

        Assert.assertEquals(villagesFieldTypeExpected, villagesFieldTypeActual);
    }

    @Test
    public void updateExpandVillage_UniqueFieldType_Yellow() {
        URL rsc = EngineActionTest.class.getResource("EngineTest5.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.withPredefinedPlayers(Gamemode.AllVsAll,
                ImmutableMap.of(
                        PlayerColor.YELLOW, e -> PlayerHandler.dummy(),
                        PlayerColor.RED, e -> PlayerHandler.dummy(),
                        PlayerColor.WHITE, e -> PlayerHandler.dummy(),
                        PlayerColor.BROWN, e -> PlayerHandler.dummy()))
                .seed(163976325L)
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.YELLOW);

        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesExpected = ImmutableSetMultimap.builder();
        builderVillagesExpected.put(island.getVillage(Hex.at(4, -3)), FieldType.JUNGLE);
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeExpected = builderVillagesExpected.build();

        Set<ExpandVillageAction> actualAction = getExpandVillageActionsUnique(engine);
        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesActual = ImmutableSetMultimap.builder();
        for (ExpandVillageAction expandVillageAction : actualAction) {
            builderVillagesActual.put(expandVillageAction.getVillage(island), expandVillageAction.getFieldType());
        }
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeActual = builderVillagesActual.build();

        Assert.assertEquals(villagesFieldTypeExpected, villagesFieldTypeActual);
    }

    @Test
    public void updateExpandVillage_Red() {
        URL rsc = EngineActionTest.class.getResource("EngineTest6.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.withPredefinedPlayers(Gamemode.AllVsAll,
                ImmutableMap.of(
                        PlayerColor.RED, e -> PlayerHandler.dummy(),
                        PlayerColor.YELLOW, e -> PlayerHandler.dummy(),
                        PlayerColor.WHITE, e -> PlayerHandler.dummy(),
                        PlayerColor.BROWN, e -> PlayerHandler.dummy()))
                .seed(16245447L)
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.RED);

        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeExpected =
                ImmutableSetMultimap.<Village, FieldType>builder()
                        .put(island.getVillage(Hex.at(-2, 1)), FieldType.SAND)
                        .put(island.getVillage(Hex.at(2, -1)), FieldType.JUNGLE)
                        .put(island.getVillage(Hex.at(2, -1)), FieldType.LAKE)
                        .put(island.getVillage(Hex.at(2, -1)), FieldType.CLEARING)
                        .put(island.getVillage(Hex.at(4, -1)), FieldType.CLEARING)
                        .put(island.getVillage(Hex.at(4, -1)), FieldType.LAKE)
                        .put(island.getVillage(Hex.at(1, 3)), FieldType.SAND)
                        .put(island.getVillage(Hex.at(1, 3)), FieldType.CLEARING)
                        .put(island.getVillage(Hex.at(1, 3)), FieldType.JUNGLE)
                        .put(island.getVillage(Hex.at(1, 3)), FieldType.ROCK)
                        .put(island.getVillage(Hex.at(1, 3)), FieldType.LAKE)
                        .build();

        Set<ExpandVillageAction> actualAction = getExpandVillageActionsUnique(engine);
        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesActual = ImmutableSetMultimap.builder();
        for (ExpandVillageAction expandVillageAction : actualAction) {
            builderVillagesActual.put(expandVillageAction.getVillage(island), expandVillageAction.getFieldType());
        }
        ImmutableSetMultimap<Village, FieldType> villagesFieldTypeActual = builderVillagesActual.build();

        Assert.assertEquals(villagesFieldTypeExpected, villagesFieldTypeActual);
    }

    @Test
    public void updateExpandVillage_RedWithBuildings() {
        //TODO TEST HEXES
        URL rsc = EngineActionTest.class.getResource("EngineTest7.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.withPredefinedPlayers(Gamemode.AllVsAll,
                ImmutableMap.of(
                        PlayerColor.RED, e -> PlayerHandler.dummy(),
                        PlayerColor.WHITE, e -> PlayerHandler.dummy(),
                        PlayerColor.BROWN, e -> PlayerHandler.dummy(),
                        PlayerColor.YELLOW, e -> PlayerHandler.dummy()))
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);
        Assert.assertTrue(engine.getCurrentPlayer().getColor() == PlayerColor.RED);

        ImmutableSetMultimap.Builder<Village, FieldType> builderVillagesExpected = ImmutableSetMultimap.builder();
        builderVillagesExpected.put(island.getVillage(Hex.at(-2, 1)), FieldType.SAND);
        builderVillagesExpected.put(island.getVillage(Hex.at(2, -1)), FieldType.JUNGLE);
        builderVillagesExpected.put(island.getVillage(Hex.at(2, -1)), FieldType.LAKE);
        builderVillagesExpected.put(island.getVillage(Hex.at(2, -1)), FieldType.CLEARING);
        builderVillagesExpected.put(island.getVillage(Hex.at(1, 3)), FieldType.SAND);
        builderVillagesExpected.put(island.getVillage(Hex.at(1, 3)), FieldType.CLEARING);
        builderVillagesExpected.put(island.getVillage(Hex.at(1, 3)), FieldType.JUNGLE);
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
