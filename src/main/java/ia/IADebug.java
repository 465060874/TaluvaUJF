package ia;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerHandler;
import engine.PlayerTurn;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkArgument;

public class IADebug implements PlayerHandler {

    private final int branchingFactor;
    private final int depth;
    private final Heuristics heuristics;

    public IADebug(int branchingFactor, int depth) {
        this(branchingFactor, depth, new BasicHeuristics());
    }

    public IADebug(int branchingFactor, int depth, Heuristics heuristics) {
        this.branchingFactor = branchingFactor;
        this.depth = depth;
        this.heuristics = heuristics;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
        checkArgument(step == EngineStatus.TurnStep.TILE, "IA does not allow starting turn for BUILD step");
        AtomicBoolean cancelled = new AtomicBoolean(false);
        IAAlgorithm algorithm = new AlphaBetaAlgorithm(branchingFactor, depth, heuristics,
                engine.copyWithoutObservers(), cancelled);
        return Platform.isFxApplicationThread()
                ? new BotPlayerTurnFx(engine, algorithm, cancelled)
                : new BotPlayerTurn(engine, algorithm);
    }
}
