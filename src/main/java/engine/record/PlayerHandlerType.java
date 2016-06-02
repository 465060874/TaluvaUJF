package engine.record;

import engine.PlayerHandler;
import ia.IA;

enum PlayerHandlerType {

    IA_FACILE,

    IA_MOYEN,

    IA_DIFFICILE,

    HUMAIN;

    public static PlayerHandlerType valueOf(PlayerHandler handler) {
        if (handler instanceof IA) {
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
}
