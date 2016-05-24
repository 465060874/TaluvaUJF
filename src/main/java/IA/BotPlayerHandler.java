package IA;

import engine.Engine;
import engine.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class BotPlayerHandler implements PlayerHandler {

    public static PlayerHandler.Factory factory( int branch, int depth ){
        return engine -> new BotPlayerHandler(engine, branch, depth);
    }

    private final Engine engine;
    private final BotPlayer bot;
    private Move move;
    private int depth;

    public BotPlayerHandler(Engine engine, int branch, int depth) {
        this.engine = engine;
        //this.bot = new BotPlayer(branch, new RandomHeuristics(engine.getRandom()), engine);
        this.bot = new BotPlayer(branch, new BasicHeuristics(), engine);
        this.depth = depth;
    }

    @Override
    public void startTileStep() {
        long startTime = System.nanoTime();
        move = bot.play(depth);
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
