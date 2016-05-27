package IA;

import engine.PlayerHandler;

public enum IADifficulty {
    FACILE {
        @Override
        public PlayerHandler.Factory create() {
            return PlayerHandler.dumbFactory();
        }
    },

    MOYEN {
        @Override
        public PlayerHandler.Factory create() {
            return BotPlayerHandler.factory(16, 0);
        }
    },

    DIFFICILE {
        @Override
        public PlayerHandler.Factory create() {
            return BotPlayerHandler.factory(16, 1);
        }
    };

    public abstract PlayerHandler.Factory create();
}
