package engine;

import com.google.common.collect.Iterables;
import engine.action.*;

import java.util.ArrayList;
import java.util.List;

public interface PlayerHandler {

    void startTileStep();

    void startBuildStep();

    void cancel();

    interface Factory {

        PlayerHandler create(Engine engine);
    }

    static PlayerHandler dummy() {
        return DummyPlayerHandler.INSTANCE;
    }

    static Factory dummyFactory() {
        return (engine) -> DummyPlayerHandler.INSTANCE;
    }

    static Factory dumbFactory() {
        return (engine) -> new DumbPlayerHandler(engine);
    }
}

enum DummyPlayerHandler implements PlayerHandler {

    INSTANCE;

    @Override
    public void startTileStep() {
    }

    @Override
    public void startBuildStep() {
    }

    @Override
    public void cancel() {
    }
}

class DumbPlayerHandler implements PlayerHandler {

    private final Engine engine;

    public DumbPlayerHandler(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void startTileStep() {
        List<Placement> placements = new ArrayList<>();
        if (engine.getRandom().nextInt(3) < 2) {
            for (Iterable<SeaPlacement> seaPlacements : engine.getSeaPlacements().values()) {
                Iterables.addAll(placements, seaPlacements);
            }
        }
        else {
            for (Iterable<VolcanoPlacement> volcanoPlacements : engine.getVolcanoPlacements().values()) {
                Iterables.addAll(placements, volcanoPlacements);
            }
        }

        int choice = engine.getRandom().nextInt(placements.size());
        Placement placement = placements.get(choice);
        engine.place(placement);
    }

    @Override
    public void startBuildStep() {
        List<Action> actions = new ArrayList<>();
        for (Iterable<BuildAction> placements : engine.getBuildActions().values()) {
            Iterables.addAll(actions, placements);
        }
        for (Iterable<ExpandAction> placements : engine.getExpandActions().values()) {
            Iterables.addAll(actions, placements);
        }

        int choice = engine.getRandom().nextInt(actions.size());
        Action action = actions.get(choice);
        engine.action(action);
    }

    @Override
    public void cancel() {
        System.out.println("Dammit");
    }
}

