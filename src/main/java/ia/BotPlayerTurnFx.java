package ia;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerTurn;
import javafx.application.Platform;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class BotPlayerTurnFx implements PlayerTurn {

    private static long DELAY = 800;

    private final Engine engine;
    private final AtomicBoolean cancelled;
    private final IAAlgorithm algorithm;
    private final EngineStatus.TurnStep step;

    private Move move;

    BotPlayerTurnFx(Engine engine, IAAlgorithm player, EngineStatus.TurnStep step) {
        this.engine = engine;
        this.cancelled = new AtomicBoolean(false);
        this.algorithm = player;
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
        long startNanos = System.nanoTime();
        move = algorithm.play();
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        engine.logger().info("[IA] {0}ms pour determiner le coup à jouer", duration);

        waitAndThen(startNanos, step == EngineStatus.TurnStep.TILE
                ? this::tileStep
                : this::buildStep);
    }


    private void waitAndThen(long startNanos, Runnable runnable) {
        if (cancelled.get()) {
            return;
        }

        Thread thread = new Thread(() -> doWaitAndThen(startNanos, runnable));
        thread.start();
    }

    private void doWaitAndThen(long startNanos, Runnable runnable) {
        try {
            final long delay = DELAY - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            if (delay > 0) {
                Thread.sleep(delay);
            }
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
            waitAndThen(System.nanoTime(), this::buildStep);
        }
    }

    private void buildStep() {
        if (!cancelled.get()) {
            engine.action(move.buildingAction);
        }
    }
}
