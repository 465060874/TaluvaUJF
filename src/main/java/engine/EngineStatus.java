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
 *          | Running create int * TurnStep
 *          | Finished create FinishReason * (Player list)
 */
public abstract class EngineStatus {

    private EngineStatus() {
    }

    public abstract int getTurn();

    public enum TurnStep {
        TILE,
        BUILD
    }

    public static final EngineStatus PENDING_START = new EngineStatus() {
        @Override
        public int getTurn() {
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
        LAST_STANDING,
    }

    public static final class Finished extends EngineStatus {

        private final int turn;
        private final FinishReason reason;
        private final List<Player> winners;

        Finished(int turn, FinishReason reason, List<Player> winners) {
            this.turn = turn;
            this.reason = reason;
            this.winners = ImmutableList.copyOf(winners);
        }

        @Override
        public int getTurn() {
            return turn;
        }

        public FinishReason getWinReason() {
            return reason;
        }

        public List<Player> getWinners() {
            return winners;
        }
    }
}
