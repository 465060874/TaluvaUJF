package ia;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerTurn;

import java.util.concurrent.TimeUnit;

class BotPlayerTurn implements PlayerTurn {

    private final Engine engine;
    private final IAAlgorithm algorithm;
    private final EngineStatus.TurnStep step;

    BotPlayerTurn(Engine engine, IAAlgorithm player, EngineStatus.TurnStep step) {
        this.engine = engine;
        this.algorithm = player;
        this.step = step;

        play();
    }

    @Override
    public void cancel() {
        throw new IllegalStateException();
    }

    private void play() {
        engine.logger().info("[IA] Starting");
        long startTime = System.nanoTime();
        Move move = algorithm.play();
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer", duration);

        if (step == EngineStatus.TurnStep.TILE) {
            engine.action(move.tileAction);
        }
        if (engine.getStatus() instanceof EngineStatus.Finished) {
            return;
        }

        engine.action(move.buildingAction);
    }
}
