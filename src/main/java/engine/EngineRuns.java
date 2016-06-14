package engine;

import data.PlayerColor;
import ia.IA;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Test brute-force qui enchaine un certain nombre de parties
 */
public class EngineRuns {

    private static final int COUNT = 50;

    public static void main(String[] args) {
        double hashFactorSum = 0;
        double durationSum = 0;
        Engine engine = null;
        for (int i = 0; i < COUNT; i++) {
            engine = EngineBuilder.allVsAll()
                    .logLevel(Level.INFO)
                    .player(PlayerColor.RED, IA.MOYEN)
                    .player(PlayerColor.WHITE, IA.MOYEN)
                    .build();

            long startTime = System.nanoTime();
            engine.start();

            while (!(engine.getStatus() instanceof EngineStatus.Finished));
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            durationSum += duration;

            hashFactorSum += engine.getIsland().getHashFactor();
            engine.logger().info("  Fini ({0}) après {1}ms",
                    ((EngineStatus.Finished) engine.getStatus()).getWinReason(),
                    duration);
        }

        engine.logger().info("Temps moyen d''execution : {0} ms", durationSum / COUNT);
        engine.logger().info("Etalement moyen de la table de hachage : {0}", percent(hashFactorSum / COUNT));
    }

    private static String percent(double hashFactor) {
        return new DecimalFormat("0.00 %").format(hashFactor);
    }
}
