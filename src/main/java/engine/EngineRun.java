package engine;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import data.BuildingType;
import data.PlayerColor;
import engine.action.BuildAction;
import engine.action.ExpandAction;
import engine.action.SeaPlacement;
import engine.action.VolcanoPlacement;
import map.IslandIO;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class EngineRun {

    public static void main(String[] args) {
        Engine engine = new EngineBuilder()
                .gamemode(Gamemode.FourPlayer)
                .player(PlayerColor.RED, PlayerHandler.dumbFactory())
                .player(PlayerColor.WHITE, PlayerHandler.dumbFactory())
                .player(PlayerColor.BROWN, PlayerHandler.dumbFactory())
                .player(PlayerColor.YELLOW, PlayerHandler.dumbFactory())
                .build();
        engine.registerObserver(new EngineLogger(engine));
        engine.start();

        File destFile = new File(System.nanoTime() + ".island");
        IslandIO.write(Files.asCharSink(destFile, StandardCharsets.UTF_8), engine.getIsland());
    }

    private static class EngineLogger implements EngineObserver {

        private final Engine engine;

        private EngineLogger(Engine engine) {
            this.engine = engine;
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
        public void onTilePlacementOnSea(SeaPlacement placement) {

        }

        @Override
        public void onTilePlacementOnVolcano(VolcanoPlacement placement) {

        }

        @Override
        public void onBuild(BuildAction action) {
            System.out.println("* Build");
            printRemainingBuilding();
        }

        @Override
        public void onExpand(ExpandAction action) {
            System.out.println("* Expansion");
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
