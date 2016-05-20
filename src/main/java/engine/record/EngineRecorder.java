package engine.record;

import com.google.common.collect.ImmutableList;
import data.PlayerColor;
import data.VolcanoTile;
import engine.Engine;
import engine.EngineObserver;
import engine.Gamemode;
import engine.Player;
import engine.action.*;

import java.util.List;

public class EngineRecorder {

    private final Engine engine;
    private final Gamemode gamemode;

    private ImmutableList.Builder<PlayerColor> colors;
    private ImmutableList.Builder<VolcanoTile> tiles;
    private ImmutableList.Builder<Action> actions;

    public static EngineRecorder install(Engine engine) {
        return new EngineRecorder(engine);
    }

    private EngineRecorder(Engine engine) {
        this.engine = engine;
        this.gamemode = engine.getGamemode();
        engine.registerObserver(new Observer());
    }

    private class Observer implements EngineObserver {

        public void onStart() {
            engine.getPlayers().stream()
                    .map(Player::getColor)
                    .forEach(colors::add);
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

        public void onWin(WinReason reason, List<Player> winners) {
        }
    }

    public EngineRecord getRecord() {
        return new EngineRecord(gamemode, colors.build(), tiles.build(), actions.build());
    }
}
