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
    private final String prefix;

    public EngineLoggerObserver(Engine engine) {
        this(engine, "");
    }

    public EngineLoggerObserver(Engine engine, String prefix) {
        this.engine = engine;
        this.prefix = prefix.endsWith(" ") ? prefix : prefix + " ";
    }

    @Override
    public void onStart() {
        engine.logger().severe("{0}Engine starting with seed {1}", prefix, Long.toString(engine.getSeed()));
    }

    @Override
    public void onTileStackChange(boolean cancelled) {
    }

    @Override
    public void onTileStepStart(boolean cancelled) {
        engine.logger().info("{0}* Turn {1} {2} {3}-{4} ({5} tiles remaining)",
                prefix,
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
        engine.logger().info("{0}  Placed on sea {1} {2}", prefix, placement.getHex1(), placement.getOrientation());
    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction placement) {
        engine.logger().info("{0}  Placed on volcano {1} {2} at level {3}",
                prefix,
                placement.getVolcanoHex(),
                placement.getOrientation(),
                engine.getIsland().getField(placement.getVolcanoHex()).getLevel());
    }

    @Override
    public void onBuild(PlaceBuildingAction action) {
        engine.logger().info("{0}  Built a {1} at {2}", prefix, action.getType(), action.getHex());
        logRemainingBuilding();
    }

    @Override
    public void onExpand(ExpandVillageAction action) {
        engine.logger().info("{0}  Expanded a village at {1} towards {2}",
                prefix,
                action.getVillageHex(),
                action.getFieldType());
        logRemainingBuilding();
    }

    private void logRemainingBuilding() {
        for (Player player : engine.getPlayers()) {
            engine.logger().info("{0}  [{1}] Hut({2}) Temple({3}) Tower({4})",
                    prefix,
                    Strings.padEnd(player.getColor().toString(), 6, ' '),
                    player.getBuildingCount(BuildingType.HUT),
                    player.getBuildingCount(BuildingType.TEMPLE),
                    player.getBuildingCount(BuildingType.TOWER));
        }
    }

    @Override
    public void onEliminated(Player eliminated) {
        engine.logger().info("{0}!!! Eliminated: {1} !!!", prefix, eliminated.getColor());
    }

    @Override
    public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
        engine.logger().info("{0}!!! Winner(s): {1} ({2}) !!!",
                prefix,
                winners.stream().map(Player::getColor).collect(toList()),
                reason);
    }
}
