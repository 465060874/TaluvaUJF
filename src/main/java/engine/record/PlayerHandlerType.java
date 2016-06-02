package engine.record;

import engine.PlayerHandler;
import ia.IA;

enum PlayerHandlerType {

    IA_FACILE {
        @Override
        public PlayerHandler getHandler(PlayerHandler humanPlayerHandler) {
            return IA.FACILE;
        }
    },

    IA_MOYEN {
        @Override
        public PlayerHandler getHandler(PlayerHandler humanPlayerHandler) {
            return IA.MOYEN;
        }
    },

    IA_DIFFICILE {
        @Override
        public PlayerHandler getHandler(PlayerHandler humanPlayerHandler) {
            return IA.DIFFICILE;
        }
    },

    HUMAIN {
        @Override
        public PlayerHandler getHandler(PlayerHandler humanPlayerHandler) {
            return humanPlayerHandler;
        }
    };

    public static PlayerHandlerType valueOf(PlayerHandler handler) {
        if (handler instanceof RecordPlayerHandler) {
            return valueOf(((RecordPlayerHandler) handler).wrapped);
        }
        else if (handler instanceof IA) {
            switch ((IA) handler) {
                case FACILE: return IA_FACILE;
                case MOYEN: return IA_MOYEN;
                case DIFFICILE: return IA_DIFFICILE;
            }

            throw new EngineRecord.Exception("Unkown type handler for "
                    + IA.class.getName() + "." + ((IA) handler).name());
        }
        else if (handler.isHuman()) {
            return HUMAIN;
        }
        else {
            throw new EngineRecord.Exception("Unkown type handler for "
                    + handler.getClass().getCanonicalName());
        }
    }

    public abstract PlayerHandler getHandler(PlayerHandler humanPlayerHandler);
}
