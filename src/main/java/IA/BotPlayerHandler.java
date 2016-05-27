package IA;

import engine.Engine;
import engine.PlayerHandler;
import engine.action.BuildingAction;
import engine.action.TileAction;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;

public class BotPlayerHandler implements PlayerHandler {

    public static PlayerHandler.Factory factory( int branch, int depth ){
        return engine -> new BotPlayerHandler(engine, branch, depth);
    }

    private final Engine engine;
    private final BotPlayer bot;
    private final int depth;

    private boolean isFx;
    private TileAction tileAction;
    private BuildingAction buildingAction;

    public BotPlayerHandler(Engine engine, int branch, int depth) {
        this.engine = engine;
        //this.bot = new BotPlayer(branch, new RandomHeuristics(engine.getRandom()), engine);
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
        engine.logger().info("[IA] Starting");
        long startTime = System.nanoTime();
        Move move = bot.play(depth);
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer", duration);

        this.tileAction = move.tileAction;
        this.buildingAction = move.buildingAction;
        if (isFx) {
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
        engine.action(buildingAction);
    }

    @Override
    public void cancel() {
    }
}
