package engine;

public interface PlayerHandler {

    boolean isHuman();

    PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step);

    static PlayerHandler dummy() {
        return DummyPlayerHandler.INSTANCE;
    }
}

enum DummyPlayerHandler implements PlayerHandler, PlayerTurn {

    INSTANCE;

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
        return this;
    }

    @Override
    public void cancel() {
    }
}
