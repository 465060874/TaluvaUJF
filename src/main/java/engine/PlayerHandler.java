package engine;

import com.google.common.collect.Iterables;
import data.BuildingType;
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

    DumbPlayerHandler(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void startTileStep() {
        List<TileAction> tileActions = new ArrayList<>();
        if (engine.getVolcanoPlacements().size() == 0 || engine.getRandom().nextInt(3) < 2) {
            for (Iterable<SeaTileAction> seaPlacements : engine.getSeaPlacements().values()) {
                Iterables.addAll(tileActions, seaPlacements);
            }
        }
        else {
            for (Iterable<VolcanoTileAction> volcanoPlacements : engine.getVolcanoPlacements().values()) {
                Iterables.addAll(tileActions, volcanoPlacements);
            }
        }

        int choice = engine.getRandom().nextInt(tileActions.size());
        TileAction tileAction = tileActions.get(choice);
        engine.action(tileAction);
    }

    @Override
    public void startBuildStep() {
        List<BuildingAction> buildTowerOrTemple = new ArrayList<>();
        List<BuildingAction> buildHut = new ArrayList<>();
        List<BuildingAction> expandVillage = new ArrayList<>();
        for (Iterable<PlaceBuildingAction> placements : engine.getBuildActions().values()) {
            for (PlaceBuildingAction action : placements) {
                if (action.getType() == BuildingType.HUT) {
                    buildHut.add(action);
                }
                else {
                    buildTowerOrTemple.add(action);
                }
            }
        }
        for (Iterable<ExpandVillageAction> placements : engine.getExpandActions().values()) {
            Iterables.addAll(expandVillage, placements);
        }

        List<BuildingAction> buildingActions;
        if (!buildTowerOrTemple.isEmpty()) {
            buildingActions = buildTowerOrTemple;
        }
        else if (!expandVillage.isEmpty()) {
            buildingActions = expandVillage;
        }
        else {
            buildingActions = buildHut;
        }

        int choice = engine.getRandom().nextInt(buildingActions.size());
        BuildingAction buildingAction = buildingActions.get(choice);
        engine.action(buildingAction);
    }

    @Override
    public void cancel() {
        System.out.println("Dammit");
    }
}

