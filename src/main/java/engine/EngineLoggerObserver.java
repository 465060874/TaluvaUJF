package engine;

import com.google.common.base.Strings;
import data.BuildingType;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class EngineLoggerObserver implements EngineObserver {

    private final Engine engine;

    public EngineLoggerObserver(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void onStart() {
        engine.logger().severe("Engine starting with seed {0}", Long.toString(engine.getSeed()));
    }

    @Override
    public void onTileStackChange(boolean cancelled) {
    }

    @Override
    public void onTileStepStart(boolean cancelled) {
        engine.logger().info("* Turn {0} {1} {2}-{3} ({4} tiles remaining)",
                engine.getStatus().getTurn(),
                engine.getCurrentPlayer().getColor(),
                engine.getVolcanoTileStack().current().getLeft(),
                engine.getVolcanoTileStack().current().getRight(),
                engine.getVolcanoTileStack().size());
    }

    @Override
    public void onBuildStepStart(boolean cancelled) {
    }

    @Override
    public void onTilePlacementOnSea(SeaTileAction placement) {
        engine.logger().info("  Placed on sea {0} {1}", placement.getHex1(), placement.getOrientation());
    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction placement) {
        engine.logger().info("  Placed on volcano {0} {1} at level {2}",
                placement.getVolcanoHex(),
                placement.getOrientation(),
                engine.getIsland().getField(placement.getVolcanoHex()).getLevel());
    }

    @Override
    public void onBuild(PlaceBuildingAction action) {
        engine.logger().info("  Built a {0} at {1}", action.getType(), action.getHex());
        logRemainingBuilding();
    }

    @Override
    public void onExpand(ExpandVillageAction action) {
        engine.logger().info("  Expanded a village at {0} towards {1}",
                action.getVillageHex(), action.getFieldType());
        logRemainingBuilding();
    }

    private void logRemainingBuilding() {
        for (Player player : engine.getPlayers()) {
            engine.logger().info("  [{0}] Hut({1}) Temple({2}) Tower({3})",
                    Strings.padEnd(player.getColor().toString(), 6, ' '),
                    player.getBuildingCount(BuildingType.HUT),
                    player.getBuildingCount(BuildingType.TEMPLE),
                    player.getBuildingCount(BuildingType.TOWER));
        }
    }

    @Override
    public void onEliminated(Player eliminated) {
        engine.logger().info("!!! Eliminated: {0} !!!", eliminated.getColor());
    }

    @Override
    public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
        engine.logger().info("!!! Winner(s): {0} ({1}) !!!",
                winners.stream().map(Player::getColor).collect(toList()),
                reason);
    }
}
