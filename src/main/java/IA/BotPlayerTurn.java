package IA;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerTurn;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotPlayerTurn implements PlayerTurn {

    private static long DELAY = 500;

    private final Engine engine;
    private final BotPlayer bot;
    private final EngineStatus.TurnStep step;

    private Move move;

    public BotPlayerTurn(Engine engine, int branch, int depth, Heuristics heuristics, EngineStatus.TurnStep step) {
        this.engine = engine;
        this.bot = new BotPlayer(branch, depth, heuristics, engine, new AtomicBoolean(false));
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
        move = bot.play();
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer", duration);

        if (step == EngineStatus.TurnStep.TILE) {
            engine.action(move.tileAction);
        }
        engine.action(move.buildingAction);
    }
}
