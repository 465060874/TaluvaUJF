package engine;

public interface PlayerHandler {

    void startTileStep();

    void startBuildStep();

    interface Factory {

        PlayerHandler create(Engine engine);
    }
}
