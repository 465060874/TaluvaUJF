package engine;

public interface PlayerHandler {

    void startTileStep();

    void startBuildStep();

    interface Factory {

        PlayerHandler create(Engine engine);
    }

    class Dummy implements PlayerHandler {

        @Override
        public void startTileStep() {
        }

        @Override
        public void startBuildStep() {
        }
    }
}
