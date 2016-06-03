package engine.record;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerHandler;
import engine.PlayerTurn;
import engine.action.Action;

import java.util.Iterator;

class RecordPlayerHandler implements PlayerHandler {

    private final Iterator<Action> actions;
    final PlayerHandler wrapped;

    RecordPlayerHandler(Iterator<Action> actions, PlayerHandler wrapped) {
        this.actions = actions;
        this.wrapped = wrapped;
    }

    @Override
    public boolean isHuman() {
        return !actions.hasNext() && wrapped.isHuman();
    }

    @Override
    public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
        if (actions.hasNext()) {
            Action nextAction = actions.next();
            if (actions.hasNext()) {
                return step == EngineStatus.TurnStep.TILE
                        ? new RecordPlayerTurn(engine, nextAction, actions.next())
                        : new RecordPlayerTurn(engine, nextAction);
            }
            else {
                return wrapped.startTurn(engine, step);
            }
        }
        else {
            return wrapped.startTurn(engine, step);
        }
    }

    private static class RecordPlayerTurn implements PlayerTurn {

        public RecordPlayerTurn(Engine engine, Action... actions) {
            for (Action action : actions) {
                engine.action(action);
            }
        }

        @Override
        public void cancel() {
        }
    }
}
