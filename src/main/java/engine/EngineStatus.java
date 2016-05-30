package engine;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Indique le statut de l'engine
 *   L'équivalent en caml avec des types algébriques :
 *     type TurnStep = ...;;
 *     type FinishReason = ...;;
 *     type EngineStatus =
 *          | PENDING_START
 *          | Running of int * TurnStep
 *          | Finished of int * FinishReason * (Player list)
 */
public abstract class EngineStatus {

    private EngineStatus() {
    }

    public abstract int getTurn();

    public abstract TurnStep getStep();

    public enum TurnStep {
        TILE,
        BUILD
    }

    public static final EngineStatus PENDING_START = new EngineStatus() {
        @Override
        public int getTurn() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TurnStep getStep() {
            throw new UnsupportedOperationException();
        }
    };

    public static final class Running extends EngineStatus {

        int turn;
        TurnStep step;

        Running() {
            this.turn = 0;
            this.step = TurnStep.TILE;
        }

        private Running(int turn, TurnStep step) {
            this.turn = turn;
            this.step = step;
        }

        @Override
        public int getTurn() {
            return turn;
        }

        @Override
        public TurnStep getStep() {
            return step;
        }

        Running copy() {
            return new Running(turn, step);
        }
    }

    public enum FinishReason {
        NO_MORE_TILES,
        TWO_BUILDING_TYPES,
        TEAM_THREE_BUILDING_TYPES,
        LAST_STANDING,
    }

    public static final class Finished extends EngineStatus {

        private final int turn;
        private final TurnStep step;
        private final FinishReason reason;
        private final List<Player> winners;

        Finished(Running running, FinishReason reason, List<Player> winners) {
            this.turn = running.turn;
            this.step = running.step;
            this.reason = reason;
            this.winners = ImmutableList.copyOf(winners);
        }

        @Override
        public int getTurn() {
            return turn;
        }

        @Override
        public TurnStep getStep() {
            return step;
        }

        public FinishReason getWinReason() {
            return reason;
        }

        public List<Player> getWinners() {
            return winners;
        }
    }
}
