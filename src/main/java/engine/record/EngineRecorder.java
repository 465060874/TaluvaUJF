package engine.record;

import data.PlayerColor;
import data.VolcanoTile;
import engine.*;
import engine.action.*;

import java.util.ArrayList;
import java.util.List;

public class EngineRecorder {

    private final Engine engine;

    private final Gamemode gamemode;
    private final List<PlayerColor> colors;
    private final List<PlayerHandlerType> handlers;
    private final List<VolcanoTile> tiles;
    private final List<Action> actions;
    private int actionsIndex;

    public static EngineRecorder install(Engine engine) {
        return new EngineRecorder(engine);
    }

    private EngineRecorder(Engine engine) {
        this.engine = engine;
        engine.registerObserver(new Observer());

        this.gamemode = engine.getGamemode();
        this.colors = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.actionsIndex = 0;
    }

    private class Observer extends EngineObserver.Dummy {

        public void onStart() {
            for (Player player : engine.getPlayers()) {
                colors.add(player.getColor());
                handlers.add(PlayerHandlerType.valueOf(player.getHandler()));
            }
            tiles.addAll(engine.getVolcanoTileStack().asList());
        }

        @Override
        public void onCancelTileStep() {
            actionsIndex--;
        }

        @Override
        public void onCancelBuildStep() {
            actionsIndex--;
        }

        @Override
        public void onRedoTileStep() {
            actionsIndex++;
        }

        @Override
        public void onRedoBuildStep() {
            actionsIndex++;
        }

        public void onTilePlacementOnSea(SeaTileAction action) {
            actions.add(action);
            actionsIndex++;
        }

        public void onTilePlacementOnVolcano(VolcanoTileAction action) {
            actions.add(action);
            actionsIndex++;
        }

        public void onBuild(PlaceBuildingAction action) {
            actions.add(action);
            actionsIndex++;
        }

        public void onExpand(ExpandVillageAction action) {
            actions.add(action);
            actionsIndex++;
        }

        public void onEliminated(Player eliminated) {
        }

        public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
        }

        public void onBeforeExpand(ExpandVillageAction action) {
            return;
        }
    }

    public EngineRecord getRecord() {
        return new EngineRecord(gamemode, colors, handlers, tiles, actions.subList(0, actionsIndex));
    }
}
