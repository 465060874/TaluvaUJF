package engine;

public interface PlayerTurn {

    void cancel();

    static PlayerTurn dummy() {
        return DummyPlayerHandler.INSTANCE;
    }
}
