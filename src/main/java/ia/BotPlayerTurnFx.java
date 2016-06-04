package ia;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerTurn;
import javafx.application.Platform;
import util.FxUncaughtExceptionHandler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class BotPlayerTurnFx implements PlayerTurn {

    private static long DELAY = 600;

    private final Engine engine;
    private final AtomicBoolean cancelled;
    private final IAAlgorithm algorithm;

    private Move move;

    BotPlayerTurnFx(Engine engine, IAAlgorithm player, AtomicBoolean cancelled) {
        this.engine = engine;
        this.cancelled = cancelled;
        this.algorithm = player;

        this.move = null;
        runThread(this::doPlay);
    }

    private void runThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        FxUncaughtExceptionHandler.install(thread, engine);
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

        waitAndThen(startNanos, this::tileStep);
    }


    private void waitAndThen(long startNanos, Runnable runnable) {
        if (cancelled.get()) {
            return;
        }

        runThread(() -> doWaitAndThen(startNanos, runnable));
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
            if (engine.getStatus() instanceof EngineStatus.Finished) {
                return;
            }

            waitAndThen(System.nanoTime(), this::buildStep);
        }
    }

    private void buildStep() {
        if (!cancelled.get()) {
            engine.action(move.buildingAction);
        }
    }
}
