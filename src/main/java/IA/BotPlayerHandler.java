package IA;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerHandler;
import engine.PlayerTurn;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class BotPlayerHandler implements PlayerHandler {

    public static PlayerHandler.Factory factory(IAAlgorithm.Factory factory) {
        return engine -> new BotPlayerHandler(engine, factory);
    }

    private final Engine engine;
    private final IAAlgorithm.Factory playerSupplier;

    public BotPlayerHandler(Engine engine, IAAlgorithm.Factory playerFactory) {
        this.engine = engine;
        this.playerSupplier = playerFactory;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(EngineStatus.TurnStep step) {
        IAAlgorithm player = playerSupplier.create(engine, new AtomicBoolean(false));
        return Platform.isFxApplicationThread()
                ? new BotPlayerTurnFx(engine, player, step)
                : new BotPlayerTurn(engine, player, step);
    }
}
