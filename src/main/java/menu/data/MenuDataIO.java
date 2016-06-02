package menu.data;

import ia.IA;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.io.Files;
import data.PlayerColor;
import javafx.scene.image.Image;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static com.google.common.base.MoreObjects.firstNonNull;

class MenuDataIO {

    private static final File FILE = new File("menu.data");
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM HH:mm");

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

        String soloColorStr = (String) properties.getOrDefault("solo-color", PlayerColor.WHITE.name());
        PlayerColor soloColor = PlayerColor.valueOf(soloColorStr);
        String soloDifficultyStr = (String) properties.getOrDefault("solo-difficulty", IA.FACILE.name());
        IA soloDifficulty = IA.valueOf(soloDifficultyStr);

        String multiModeStr = (String) properties.getOrDefault("multi-mode", MultiMode.TWO_PLAYER.name());
        MultiMode multiMode = MultiMode.valueOf(multiModeStr);

        ImmutableList.Builder<SavedGame> savedGamesBuilder = ImmutableList.builder();
        File savesDir = new File("Saves");
        File[] saveFiles = firstNonNull(
                savesDir.listFiles((dir, name) -> name.endsWith(".taluva")),
                new File[0]);
        for (File file : saveFiles) {
            String basename = file.getName().split("\\.")[0];
            long millis = Long.parseLong(basename);
            String date = DATE_FORMATTER.format(new Date(millis));
            File imageFile = new File(file.getParent(), basename + ".png");
            try {
                Image image = new Image(new FileInputStream(imageFile));
                savedGamesBuilder.add(new SavedGame(date, file, image));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        ImmutableList<SavedGame> savedGames = Ordering.natural()
                .onResultOf(SavedGame::getDate)
                .reverse()
                .immutableSortedCopy(savedGamesBuilder.build());

        return new MenuData(soloColor, soloDifficulty, multiMode, savedGames);
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
}
