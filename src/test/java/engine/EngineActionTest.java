package engine;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
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

}
