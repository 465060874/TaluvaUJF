package IA;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerHandler;
import engine.PlayerTurn;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public enum IA implements PlayerHandler {

    FACILE {
        protected IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled) {
            return new EasyAlgorithm(engine, cancelled);
        }
    },

    MOYEN {
        protected IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled) {
            return new MinMaxAlgorithm(16, 0, new BasicHeuristics(), engine, cancelled);
        }
    },

    DIFFICILE {
        protected IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled) {
            return new MinMaxAlgorithm(16, 2, new BasicHeuristics(), engine, cancelled);
        }
    };

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
        IAAlgorithm algorithm = createAlgorithm(engine, new AtomicBoolean(false));
        return Platform.isFxApplicationThread()
                ? new BotPlayerTurnFx(engine, algorithm, step)
                : new BotPlayerTurn(engine, algorithm, step);
    }

    protected abstract IAAlgorithm createAlgorithm(Engine engine, AtomicBoolean cancelled);
}
