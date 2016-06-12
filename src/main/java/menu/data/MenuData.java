package menu.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import data.PlayerColor;
import engine.EngineBuilder;
import engine.PlayerHandler;
import engine.record.EngineRecord;
import ia.IA;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

public class MenuData {

    private MenuMode mode;

    private PlayerColor soloColor;
    private IA soloDifficulty;

    private MultiMode multiMode;

    private final ImmutableList<SavedGame> savedGames;
    private SavedGame selectedSavedGame;

    public static MenuData load() {
        return MenuDataIO.load();
    }

    MenuData(PlayerColor soloColor,
             IA soloDifficulty,
             MultiMode multiMode,
             ImmutableList<SavedGame> savedGames) {
        this.mode = null;
        this.soloColor = soloColor;
        this.soloDifficulty = soloDifficulty;
        this.multiMode = multiMode;
        this.savedGames = savedGames;
        this.selectedSavedGame = Iterables.getFirst(savedGames, null);
    }

    public MenuMode getMode() {
        return mode;
    }

    public void setMode(MenuMode mode) {
        this.mode = mode;
    }

    public PlayerColor getSoloColor() {
        return soloColor;
    }

    public void setSoloColor(PlayerColor soloColor) {
        this.soloColor = soloColor;
    }

    public IA getSoloDifficulty() {
        return soloDifficulty;
    }

    public void setSoloDifficulty(IA soloDifficulty) {
        this.soloDifficulty = soloDifficulty;
    }

    public MultiMode getMultiMode() {
        return multiMode;
    }

    public void setMultiMode(MultiMode multiMode) {
        this.multiMode = multiMode;
    }

    public ImmutableList<SavedGame> getSavedGames() {
        return savedGames;
    }

    public Optional<SavedGame> getSelectedSavedGame() {
        return Optional.ofNullable(selectedSavedGame);
    }

    public void setSelectedSavedGame(SavedGame selectedSavedGame) {
        this.selectedSavedGame = selectedSavedGame;
    }

    public void save() {
        MenuDataIO.save(this);
    }

    public EngineBuilder engineBuilder(PlayerHandler uiPlayerHandler) {
        switch (mode) {
            case SOLO: return soloEngineBuilder(uiPlayerHandler);
            case MULTIJOUEUR: return multiEngineBuilder(uiPlayerHandler);
            case CHARGER: return chargerEngineBuilder(uiPlayerHandler);
        }

        throw new IllegalStateException();
    }

    private EngineBuilder soloEngineBuilder(PlayerHandler uiPlayerHandler) {
        return EngineBuilder.allVsAll()
                .player(soloColor, uiPlayerHandler)
                .player(otherColor(soloColor), soloDifficulty);
    }

    private PlayerColor otherColor(PlayerColor soloColor) {
        Random rand = new Random();
        PlayerColor[] colors = PlayerColor.values();
        PlayerColor otherColor = soloColor;
        while (otherColor == soloColor) {
            otherColor = colors[rand.nextInt(colors.length)];
        }

        return otherColor;
    }

    private EngineBuilder multiEngineBuilder(PlayerHandler uiPlayerHandler) {
        switch (multiMode) {
            case TWO_PLAYER:
                return EngineBuilder.allVsAll()
                        .player(PlayerColor.WHITE, uiPlayerHandler)
                        .player(PlayerColor.RED, uiPlayerHandler);
            case THREE_PLAYER:
                return EngineBuilder.allVsAll()
                        .player(PlayerColor.WHITE, uiPlayerHandler)
                        .player(PlayerColor.RED, uiPlayerHandler)
                        .player(PlayerColor.YELLOW, uiPlayerHandler);
            case FOUR_PLAYER:
                return EngineBuilder.allVsAll()
                        .player(PlayerColor.WHITE, uiPlayerHandler)
                        .player(PlayerColor.RED, uiPlayerHandler)
                        .player(PlayerColor.YELLOW, uiPlayerHandler)
                        .player(PlayerColor.BROWN, uiPlayerHandler);
            case TEAM_VS_TEAM:
                return EngineBuilder.teamVsTeam()
                        .team(PlayerColor.RED, PlayerColor.WHITE, uiPlayerHandler)
                        .team(PlayerColor.BROWN, PlayerColor.YELLOW, uiPlayerHandler);
        }

        throw new IllegalStateException();
    }

    private EngineBuilder chargerEngineBuilder(PlayerHandler uiPlayerHandler) {
        CharSource source = Files.asCharSource(selectedSavedGame.getFile(), StandardCharsets.UTF_8);
        EngineRecord record = EngineRecord.load(source);
        return record.replay(uiPlayerHandler);
    }
}
