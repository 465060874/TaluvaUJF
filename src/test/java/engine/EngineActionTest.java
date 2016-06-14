package engine;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import data.FieldType;
import data.PlayerColor;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import engine.rules.SeaTileRules;
import engine.rules.VolcanoTileRules;
import map.*;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static util.SetTest.assertNoDuplicatesAndCreateSet;

public class EngineActionTest {

    public static final int LIMIT = 10;

    @Test
    public void testSeaTileActions() {
        URL rsc = EngineActionTest.class.getResource("EngineTest1.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.RED, PlayerHandler.dummy())
                .player(PlayerColor.WHITE, PlayerHandler.dummy())
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);

        Set<SeaTileAction> actual = assertNoDuplicatesAndCreateSet(engine.getSeaTileActions());

        ImmutableSet.Builder<SeaTileAction> builder = ImmutableSet.builder();
        for (int i = -LIMIT; i < LIMIT; i++) {
            for (int j = -LIMIT; j < LIMIT; j++) {
                Hex hex = Hex.at(i, j);
                for (Orientation orientation : Orientation.values()) {
                    if (!SeaTileRules.validate(island, hex, orientation).isValid()) {
                        continue;
                    }

                    builder.add(new SeaTileAction(hex, orientation));
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
                .player(PlayerColor.RED, PlayerHandler.dummy())
                .player(PlayerColor.WHITE, PlayerHandler.dummy())
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);

        Set<VolcanoTileAction> actual = assertNoDuplicatesAndCreateSet(engine.getVolcanoTileActions());

        ImmutableSet.Builder<VolcanoTileAction> builder = ImmutableSet.builder();
        for (int i = -LIMIT; i < LIMIT; i++) {
            for (int j = -LIMIT; j < LIMIT; j++) {
                Hex hex = Hex.at(i, j);
                for (Orientation orientation : Orientation.values()) {
                    if (!VolcanoTileRules.validate(island, hex, orientation).isValid()) {
                        continue;
                    }

                    builder.add(new VolcanoTileAction(hex, orientation));
                }
            }
        }
        ImmutableSet<VolcanoTileAction> expected = builder.build();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUpdatePlaceBuilding() {
        //TODO with Buildings
        //TODO with Levels
        //TODO with new tile
        URL rsc = EngineActionTest.class.getResource("EngineTest3.island");
        Island island = IslandIO.read(Resources.asCharSource(rsc, StandardCharsets.UTF_8));
        Engine engine = EngineBuilder.allVsAll()
                .player(PlayerColor.RED, PlayerHandler.dummy())
                .player(PlayerColor.WHITE, PlayerHandler.dummy())
                .logLevel(Level.INFO)
                .island(island)
                .build();
        engine.start();

        assertFalse(engine.getStatus() instanceof EngineStatus.Finished);

        ImmutableSet.Builder<Hex> builder = ImmutableSet.builder();
        for (Hex hex : island.getFields()) {
            Field field = island.getField(hex);
            if (!field.hasBuilding() && field.getLevel() == 1 && field.getType() != FieldType.VOLCANO) {
                builder.add(hex);
            }
        }

        ImmutableSet<Hex> expected = builder.build();
        Set<Hex> actual = assertNoDuplicatesAndCreateSet(engine.getPlaceBuildingActions().stream()
                .map(PlaceBuildingAction::getHex)
                .collect(Collectors.toList()));

        Assert.assertEquals(expected, actual);
    }



}
