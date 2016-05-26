package engine;

import data.PlayerColor;

import java.text.DecimalFormat;
import java.util.logging.Level;

/**
 * Test brute-force qui enchaine un certain nombre de parties
 */
public class EngineRuns {

    private static final int COUNT = 500;

    public static void main(String[] args) {
        for (int i = 0; i < COUNT; i++) {
            Engine engine = EngineBuilder.allVsAll()
                    .logLevel(Level.WARNING)
                    .player(PlayerColor.RED, PlayerHandler.dumbFactory())
                    .player(PlayerColor.WHITE, PlayerHandler.dumbFactory())
                    .build();

            engine.logger().warning("* Starting game seeded with {0}", Long.toString(engine.getSeed()));
            engine.start();

            while (!(engine.getStatus() instanceof EngineStatus.Finished));

            engine.logger().warning("  Etalement de la table de hachage : {0}",
                    percent(engine.getIsland().getHashFactor()));
            engine.logger().warning("  Finished because of {0}",
                    ((EngineStatus.Finished) engine.getStatus()).getWinReason());
        }
    }

    private static String percent(double hashFactor) {
        return new DecimalFormat("0.00 %").format(hashFactor);
    }
}
