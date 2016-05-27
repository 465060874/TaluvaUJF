package menu.data;

import IA.IADifficulty;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import data.PlayerColor;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

class MenuDataIO {

    private static final File FILE = new File("menu.data");
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static MenuData load() {
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

        String multiModeStr = (String) properties.getOrDefault("multi-mode", MultiMode.TWO_PLAYER);
        MultiMode multiMode = MultiMode.valueOf(multiModeStr);

        ImmutableList<SavedGame> savedGames = ImmutableList.of();

        return new MenuData(mode, soloColor, soloDifficulty, multiMode, savedGames);
    }

    public static void save(MenuData menuData) {
        Properties properties = new Properties();

        properties.put("mode", menuData.getMode().name());

        properties.put("solo-color", menuData.getSoloColor().name());
        properties.put("solo-difficulty", menuData.getSoloDifficulty().name());

        properties.put("multi-mode", menuData.getMultiMode().name());

        try (Writer writer = Files.asCharSink(FILE, CHARSET).openBufferedStream()) {
            properties.store(writer, "");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
