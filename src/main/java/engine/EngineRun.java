package engine;

import IA.BotPlayerHandler;
import com.google.common.io.Files;
import data.PlayerColor;
import map.IslandIO;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class EngineRun {

    public static void main(String[] args) {
        Engine engine = new EngineBuilder()
                .gamemode(Gamemode.TwoPlayer)
                .player(PlayerColor.RED, PlayerHandler.dumbFactory())
                .player(PlayerColor.WHITE, BotPlayerHandler::new)
                .build();
        engine.start();

        File destFile = new File(System.nanoTime() + ".island");
        IslandIO.write(Files.asCharSink(destFile, StandardCharsets.UTF_8), engine.getIsland());
        System.out.println("Debug");
    }
}
