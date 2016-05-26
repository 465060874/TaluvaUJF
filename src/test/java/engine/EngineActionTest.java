package engine;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import data.PlayerColor;
import data.VolcanoTile;
import engine.action.SeaTileAction;
import engine.rules.SeaTileRules;
import map.Hex;
import map.Island;
import map.IslandIO;
import map.Orientation;
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

        Set<SeaTileAction> actual = new HashSet<>();
        for (SeaTileAction action : engine.getSeaTileActions()) {
            // On vérifie l'unicité, on ne souhaite pas que l'iterable renvoyé par
            // contienne plusieurs fois le même élément
            if (!actual.add(action)) {
                fail("Duplicated elements " + action);
            }
        }

        ImmutableSet.Builder<SeaTileAction> builder = ImmutableSet.builder();
        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
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
}
