package IA;

import engine.Engine;
import engine.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class BotPlayerHandler implements PlayerHandler {

    private final Engine engine;
    private final BotPlayer bot;
    private FullMove move;

    public BotPlayerHandler(Engine engine) {
        this.engine = engine;
        this.bot = new BotPlayer(16, new RandomHeuristics());
    }

    @Override
    public void startTileStep() {
        long startTime = System.nanoTime();
        move = bot.play(engine.copyWithoutObservers(), 3);
        System.out.println("Fini : " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
        engine.place(move.placement);
    }

    @Override
    public void startBuildStep() {
        engine.action(move.action);
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException();
    }
}
