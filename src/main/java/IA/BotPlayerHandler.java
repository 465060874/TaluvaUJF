package IA;

import engine.Engine;
import engine.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class BotPlayerHandler implements PlayerHandler {

    private final Engine engine;
    private final BotPlayer bot;
    private Move move;

    public BotPlayerHandler(Engine engine) {
        this.engine = engine;
        this.bot = new BotPlayer(16, new RandomHeuristics(engine.getRandom()), engine);
    }

    @Override
    public void startTileStep() {
        long startTime = System.nanoTime();
        move = bot.play(1);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer",
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
        engine.action(move.tileAction);
    }

    @Override
    public void startBuildStep() {
        engine.action(move.buildingAction);
    }

    @Override
    public void cancel() {
    }
}
