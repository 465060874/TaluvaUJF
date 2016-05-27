package menu.data;

import IA.IADifficulty;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import data.PlayerColor;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

class MenuDataIO {

    private static final File FILE = new File("menu.data");
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    static MenuData load() {
        Properties properties = new Properties();

        if (FILE.exists()) {
            try (Reader reader = Files.asCharSource(FILE, CHARSET).openStream()) {
                properties.load(reader);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String modeStr = (String) properties.getOrDefault("mode", MenuMode.SOLO.name());
        MenuMode mode = MenuMode.valueOf(modeStr);

        String soloColorStr = (String) properties.getOrDefault("solo-color", PlayerColor.WHITE.name());
        PlayerColor soloColor = PlayerColor.valueOf(soloColorStr);
        String soloDifficultyStr = (String) properties.getOrDefault("solo-difficulty", IADifficulty.FACILE.name());
        IADifficulty soloDifficulty = IADifficulty.valueOf(soloDifficultyStr);

        String multiModeStr = (String) properties.getOrDefault("multi-mode", MultiMode.TWO_PLAYER.name());
        MultiMode multiMode = MultiMode.valueOf(multiModeStr);

        ImmutableList<SavedGame> savedGames = ImmutableList.of();

        return new MenuData(mode, soloColor, soloDifficulty, multiMode, savedGames);
    }

    private static Properties toProperties(MenuData menuData) {
        Properties properties = new Properties();

        properties.put("mode", menuData.getMode().name());

        properties.put("solo-color", menuData.getSoloColor().name());
        properties.put("solo-difficulty", menuData.getSoloDifficulty().name());

        properties.put("multi-mode", menuData.getMultiMode().name());

        return properties;
    }

    static void save(MenuData menuData) {
        Properties properties = toProperties(menuData);
        try (Writer writer = Files.asCharSink(FILE, CHARSET).openBufferedStream()) {
            properties.store(writer, "");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void debug(MenuData menuData) {
        Properties properties = toProperties(menuData);
        try {
            properties.store(new PrintWriter(System.out), "");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
