package engine;

import IA.BotPlayerHandler;
import data.PlayerColor;

import java.util.logging.Level;

public class EngineRuns {

    private static final int COUNT = 500;

    public static void main(String[] args) {
        for (int i = 0; i < COUNT; i++) {
            Engine engine = EngineBuilder.allVsAll()
                    .logLevel(Level.INFO)
                    .player(PlayerColor.RED, PlayerHandler.dumbFactory())
                    .player(PlayerColor.WHITE, BotPlayerHandler.factory(16, 2))
                    .build();
            engine.start();

            while (!(engine.getStatus() instanceof EngineStatus.Finished)) {
                continue;
            }

            engine.logger().info("Game seeded with {0} finished because of {1}",
                    Long.toString(engine.getSeed()),
                    ((EngineStatus.Finished) engine.getStatus()).getWinReason());
        }
    }
}
