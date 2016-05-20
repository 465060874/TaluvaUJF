package engine.record;

import data.VolcanoTile;
import engine.Engine;
import engine.EngineObserver;
import engine.Gamemode;
import engine.Player;
import engine.action.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class EngineRecorder implements EngineObserver {

    private final Engine engine;
    private final Gamemode gamemode;

    private List<Player> players;
    private List<VolcanoTile> tiles;
    private List<Action> actions;

    public EngineRecorder(Engine engine) {
        this.engine = engine;
        this.gamemode = engine.getGamemode();
    }

    public void onStart() {
        this.players = engine.getPlayers().stream()
                .map(Player::copyWithDummyHandler)
                .collect(toList());
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
