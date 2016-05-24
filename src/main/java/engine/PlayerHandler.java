package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.BuildingType;
import engine.action.BuildingAction;
import engine.action.PlaceBuildingAction;
import engine.action.TileAction;

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
        tileActions.addAll(engine.getSeaTileActions());
        tileActions.addAll(engine.getVolcanoTileActions());

        int choice = engine.getRandom().nextInt(tileActions.size());
        TileAction tileAction = tileActions.get(choice);
        engine.action(tileAction);
    }

    @Override
    public void startBuildStep() {
        List<BuildingAction> buildTowerOrTemple = new ArrayList<>();
        List<BuildingAction> buildHut = new ArrayList<>();
        for (PlaceBuildingAction action :
                Iterables.concat(engine.getPlaceBuildingActions(), engine.getNewPlaceBuildingActions())) {
            if (action.getType() == BuildingType.HUT) {
                buildHut.add(action);
            }
            else {
                buildTowerOrTemple.add(action);
            }
        }

        List<? extends BuildingAction> buildingActions;
        if (!buildTowerOrTemple.isEmpty()) {
            buildingActions = buildTowerOrTemple;
        }
        else if (!engine.getExpandVillageActions().isEmpty()) {
            buildingActions = ImmutableList.<BuildingAction>builder()
                    .addAll(engine.getExpandVillageActions())
                    .addAll(engine.getNewExpandVillageActions())
                    .build();
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

