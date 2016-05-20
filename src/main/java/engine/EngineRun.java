package engine;

import IA.BotPlayerHandler;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import data.BuildingType;
import data.PlayerColor;
import engine.action.*;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import engine.record.EngineRecorder;
import map.IslandIO;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class EngineRun {

    public static void main(String[] args) {
        Engine engine = new EngineBuilder()
                .gamemode(Gamemode.TwoPlayer)
                .player(PlayerColor.RED, PlayerHandler.dumbFactory())
                .player(PlayerColor.WHITE, PlayerHandler.dumbFactory())
                .build();
        engine.registerObserver(new EngineLogger(engine));
        EngineRecorder recorder = EngineRecorder.install(engine);
        engine.start();

        long nanoTime = System.nanoTime();
        File islandFile = new File(nanoTime + ".island");
        File taluvaFile = new File(nanoTime + ".taluva");

        IslandIO.write(Files.asCharSink(islandFile, StandardCharsets.UTF_8), engine.getIsland());
        recorder.getRecord().save(Files.asCharSink(taluvaFile, StandardCharsets.UTF_8));
    }

    private static class EngineLogger implements EngineObserver {

        private final Engine engine;

        private EngineLogger(Engine engine) {
            this.engine = engine;
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onTileStackChange() {
            System.out.println("[[Tiles remaining " + engine.getVolcanoTileStack().size() + "]]");
        }

        @Override
        public void onTileStepStart() {
            System.out.println(engine.getCurrentPlayer().getColor() + "'s turn");
        }

        @Override
        public void onBuildStepStart() {
        }

        @Override
        public void onTilePlacementOnSea(SeaTileAction placement) {
        }

        @Override
        public void onTilePlacementOnVolcano(VolcanoTileAction placement) {
        }

        @Override
        public void onBuild(PlaceBuildingAction action) {
            System.out.println("* Build a " + action.getType() + " at " + action.getHex());
            printRemainingBuilding();
        }

        @Override
        public void onExpand(ExpandVillageAction action) {
            System.out.println("* Expansion a village at " + action.getVillageHex()
                    + " towards " + action.getFieldType());
            printRemainingBuilding();
        }

        private void printRemainingBuilding() {
            for (Player player : engine.getPlayers()) {
                System.out.println("  [" + Strings.padEnd(player.getColor().toString(), 8, ' ') + "]"
                        + " HUT(" + player.getBuildingCount(BuildingType.HUT) + ")"
                        + " TEMPLE(" + player.getBuildingCount(BuildingType.TEMPLE) + ")"
                        + " TOWER(" + player.getBuildingCount(BuildingType.TOWER) + ")");
            }
        }

        @Override
        public void onEliminated(Player eliminated) {
            System.out.println("!!! Eliminated: " + eliminated.getColor() + " !!!");
        }

        @Override
        public void onWin(WinReason reason, List<Player> winners) {
            System.out.println("!!! Winner : "
                    + winners.stream().map(Player::getColor).collect(toList())
                    + " (" + reason + ") !!!");
        }
    }
}
