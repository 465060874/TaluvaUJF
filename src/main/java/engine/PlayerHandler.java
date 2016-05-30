package engine;

import com.google.common.collect.ImmutableList;
import data.BuildingType;
import engine.action.BuildingAction;
import engine.action.PlaceBuildingAction;
import engine.action.TileAction;

import java.util.ArrayList;
import java.util.List;

public interface PlayerHandler {

    boolean isHuman();

    PlayerTurn startTurn(EngineStatus.TurnStep step);

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

enum DummyPlayerHandler implements PlayerHandler, PlayerTurn {

    INSTANCE;

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(EngineStatus.TurnStep step) {
        return this;
    }

    @Override
    public void cancel() {
    }
}

class DumbPlayerHandler implements PlayerHandler, PlayerTurn {

    private final Engine engine;

    DumbPlayerHandler(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(EngineStatus.TurnStep step) {
        tileStep();
        buildStep();
        return this;
    }

    private void tileStep() {
        List<? extends TileAction> tileActions;
        if (engine.getVolcanoTileActions().isEmpty() || engine.getRandom().nextInt(3) == 2) {
            tileActions = engine.getSeaTileActions();
        }
        else {
            tileActions = engine.getVolcanoTileActions();
        }

        int choice = engine.getRandom().nextInt(tileActions.size());
        TileAction tileAction = tileActions.get(choice);
        engine.action(tileAction);
    }

    private void buildStep() {
        List<BuildingAction> buildTowerOrTemple = new ArrayList<>();
        List<BuildingAction> buildHut = new ArrayList<>();
        for (PlaceBuildingAction action : engine.getPlaceBuildingActions()) {
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
        // Single threaded implementation, should not happened
        throw new IllegalStateException();
    }
}

