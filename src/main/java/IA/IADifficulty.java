package IA;

import engine.PlayerHandler;

public enum IADifficulty {
    FACILE {
        @Override
        public PlayerHandler.Factory create() {
            return BotPlayerHandler.factory(
                    (engine, cancelled) -> new EasyAlgorithm(engine, cancelled));
        }
    },

    MOYEN {
        @Override
        public PlayerHandler.Factory create() {
            return BotPlayerHandler.factory(
                    (engine, cancelled) -> new MinMaxAlgorithm(16, 0, new BasicHeuristics(), engine, cancelled));
        }
    },

    DIFFICILE {
        @Override
        public PlayerHandler.Factory create() {
            return BotPlayerHandler.factory(
                    (engine, cancelled) -> new MinMaxAlgorithm(16, 2, new BasicHeuristics(), engine, cancelled));
        }
    };

    public abstract PlayerHandler.Factory create();
}
