package IA;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerTurn;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotPlayerTurnFx implements PlayerTurn {

    private static long DELAY = 500;

    private final Engine engine;
    private final AtomicBoolean cancelled;
    private final IAAlgorithm bot;
    private final EngineStatus.TurnStep step;

    private Move move;

    public BotPlayerTurnFx(Engine engine, IAAlgorithm player, EngineStatus.TurnStep step) {
        this.engine = engine;
        this.cancelled = new AtomicBoolean(false);
        this.bot = player;
        this.step = step;

        this.move = null;
        Thread thread = new Thread(this::doPlay);
        thread.start();
    }

    @Override
    public void cancel() {
        cancelled.set(true);
    }

    private void doPlay() {
        engine.logger().info("[IA] Starting");
        long startTime = System.nanoTime();
        move = bot.play();
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer", duration);

        waitAndThen(step == EngineStatus.TurnStep.TILE
                ? this::tileStep
                : this::buildStep);
    }


    private void waitAndThen(Runnable runnable) {
        if (cancelled.get()) {
            return;
        }

        Thread thread = new Thread(() -> doWaitAndThen(runnable));
        thread.start();
    }

    private void doWaitAndThen(Runnable runnable) {
        long startMillis = System.currentTimeMillis();
        try {
            final long delay = DELAY - (System.currentTimeMillis() - startMillis);
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {
        }

        if (cancelled.get()) {
            return;
        }

        Platform.runLater(runnable);
    }

    private void tileStep() {
        if (!cancelled.get()) {
            engine.action(move.tileAction);
            waitAndThen(this::buildStep);
        }
    }

    private void buildStep() {
        if (!cancelled.get()) {
            engine.action(move.buildingAction);
        }
    }
}
