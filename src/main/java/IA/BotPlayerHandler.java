package IA;

import engine.Engine;
import engine.PlayerHandler;
import engine.action.BuildingAction;
import engine.action.TileAction;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotPlayerHandler implements PlayerHandler {

    public static PlayerHandler.Factory factory(int branch, int depth) {
        return engine -> new BotPlayerHandler(engine, branch, depth);
    }

    private static long DELAY = 500;

    private final Engine engine;
    private final BotPlayer bot;
    private final int depth;

    private AtomicBoolean cancelled;
    private boolean isFx;
    private TileAction tileAction;
    private BuildingAction buildingAction;

    public BotPlayerHandler(Engine engine, int branch, int depth) {
        this.engine = engine;
        this.bot = new BotPlayer(branch, new BasicHeuristics(), engine);
        this.depth = depth;
    }

    @Override
    public void startTileStep() {
        isFx = Platform.isFxApplicationThread();
        if (isFx) {
            Thread thread = new Thread(this::doStartTileStep);
            thread.start();
        }
        else {
            doStartTileStep();
        }
    }

    private void doStartTileStep() {
        final long startMillis = System.currentTimeMillis();

        engine.logger().info("[IA] Starting");
        long startTime = System.nanoTime();
        cancelled = new AtomicBoolean();
        Move move = bot.play(depth);
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer", duration);

        this.tileAction = move.tileAction;
        this.buildingAction = move.buildingAction;
        if (isFx) {
            waitABit(startMillis);
            Platform.runLater(this::finishTileStep);
        }
        else {
            finishTileStep();
        }
    }

    private void finishTileStep() {
        engine.action(tileAction);
    }

    @Override
    public void startBuildStep() {
        if (isFx) {
            Thread thread = new Thread(this::doStartBuildStep);
            thread.start();
        }
        else {
            doStartBuildStep();
        }
    }

    private void doStartBuildStep() {
        final long startMillis = System.currentTimeMillis();

        if (isFx) {
            waitABit(startMillis);
            Platform.runLater(this::finishBuildStep);
        }
        else {
            finishBuildStep();
        }
    }

    private void finishBuildStep() {
        engine.action(buildingAction);
    }

    private void waitABit(long startMillis) {
        try {
            final long delay = DELAY - (System.currentTimeMillis() - startMillis);
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {
            // Hope this do not happen
        }
    }

    @Override
    public void cancel() {
    }
}
