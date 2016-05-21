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
        this.tiles = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    private class Observer implements EngineObserver {

        public void onStart() {
            engine.getPlayers().stream()
                    .map(Player::getColor)
                    .forEach(colors::add);
        }

        public void onTileStackChange(boolean cancelled) {
            if (cancelled) {
                tiles.remove(tiles.size() - 1);
            }
            else {
                tiles.add(engine.getVolcanoTileStack().current());
            }
        }

        public void onTileStepStart(boolean cancelled) {
            if (cancelled) {
                actions.remove(actions.size() - 1);
            }
        }

        public void onBuildStepStart(boolean cancelled) {
            if (cancelled) {
                actions.remove(actions.size() - 1);
            }
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
        return new EngineRecord(gamemode, colors, tiles, actions);
    }
}
