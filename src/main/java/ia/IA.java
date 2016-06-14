package ia;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerHandler;
import engine.PlayerTurn;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkArgument;

public enum IA implements PlayerHandler {

    FACILE {
        protected IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled) {
            return new EasyAlgorithm(engine, cancelled);
        }
    },

    MOYEN {
        protected IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled) {
            return new AlphaBetaAlgorithm(16, 0, new BasicHeuristics(), engine, cancelled);
        }
    },

    DIFFICILE {
        protected IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled) {
            return new AlphaBetaAlgorithm(16, 3, new BasicHeuristics(), engine, cancelled);
        }
    };

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
        checkArgument(step == EngineStatus.TurnStep.TILE, "IA does not allow starting turn for BUILD step");
        AtomicBoolean cancelled = new AtomicBoolean(false);
        IAAlgorithm algorithm = createAlgorithm(engine.copyWithoutObservers(), cancelled);
        return Platform.isFxApplicationThread()
                ? new BotPlayerTurnFx(engine, algorithm, cancelled)
                : new BotPlayerTurn(engine, algorithm);
    }

    protected abstract IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled);
}
