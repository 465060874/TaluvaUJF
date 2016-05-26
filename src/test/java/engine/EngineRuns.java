package engine;

import IA.BotPlayerHandler;
import data.PlayerColor;

import java.util.logging.Level;

/**
 * Test brute-force qui enchaine un certain nombre de parties
 */
public class EngineRuns {

    private static final int COUNT = 500;

    public static void main(String[] args) {
        for (int i = 0; i < COUNT; i++) {
            Engine engine = EngineBuilder.allVsAll()
                    .logLevel(Level.INFO)
                    .player(PlayerColor.RED, PlayerHandler.dumbFactory())
                    .player(PlayerColor.WHITE, BotPlayerHandler.factory(16, 2))
                    .build();

            engine.logger().info("* Starting game seeded with {0}", Long.toString(engine.getSeed()));
            engine.start();


            while (!(engine.getStatus() instanceof EngineStatus.Finished)) {
                continue;
            }

            engine.logger().info("  Finished because of {0}",
                    ((EngineStatus.Finished) engine.getStatus()).getWinReason());
        }
    }
}
