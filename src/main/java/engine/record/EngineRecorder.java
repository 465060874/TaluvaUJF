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

    public static EngineRecorder install(Engine engine) {
        return new EngineRecorder(engine);
    }

    private EngineRecorder(Engine engine) {
        this.engine = engine;
        engine.registerObserver(new Observer());

        this.gamemode = engine.getGamemode();
        this.colors = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    private class Observer implements EngineObserver {

        public void onStart() {
            for (Player player : engine.getPlayers()) {
                colors.add(player.getColor());
                handlers.add(PlayerHandlerType.valueOf(player.getHandler()));
            }
            engine.getPlayers().stream()
                    .map(Player::getColor)
                    .forEach(colors::add);
        }

        @Override
        public void onCancelTileStep() {
            tiles.remove(tiles.size() - 1);
            actions.remove(actions.size() - 1);
        }

        @Override
        public void onCancelBuildStep() {
            tiles.remove(tiles.size() - 1);
            actions.remove(actions.size() - 1);
        }

        public void onTileStackChange() {
            tiles.add(engine.getVolcanoTileStack().current());
        }

        public void onTileStepStart() {
        }

        public void onBuildStepStart() {
        }

        public void onTilePlacementOnSea(SeaTileAction action) {
            actions.add(action);
        }

        public void onTilePlacementOnVolcano(VolcanoTileAction action) {
            actions.add(action);
        }

        public void onBuild(PlaceBuildingAction action) {
            actions.add(action);
        }

        public void onExpand(ExpandVillageAction action) {
            actions.add(action);
        }

        public void onEliminated(Player eliminated) {
        }

        public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
        }
    }

    public EngineRecord getRecord() {
        return new EngineRecord(gamemode, colors, handlers, tiles, actions);
    }
}
