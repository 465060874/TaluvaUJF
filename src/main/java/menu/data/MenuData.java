package menu.data;

import IA.IA;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.PlayerColor;
import engine.EngineBuilder;
import engine.PlayerHandler;

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

    MenuData(MenuMode mode,
             PlayerColor soloColor, IA soloDifficulty,
             MultiMode multiMode,
             ImmutableList<SavedGame> savedGames) {
        this.mode = mode;
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

    public void debug() {
        MenuDataIO.debug(this);
    }

    public EngineBuilder engineBuilder(PlayerHandler uiPlayerHandler) {
        switch (mode) {
            case SOLO: return soloEngineBuilder(uiPlayerHandler);
            case MULTIJOUEUR: return multiEngineBuilder(uiPlayerHandler);
            case CHARGER: throw new UnsupportedOperationException("Not implemented yet");
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
        while (true) {
            PlayerColor color = colors[rand.nextInt(colors.length)];
            if (color != soloColor) {
                return color;
            }
        }
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
                        .team(PlayerColor.BROWN, PlayerColor.WHITE, uiPlayerHandler)
                        .team(PlayerColor.RED, PlayerColor.YELLOW, uiPlayerHandler);
        }

        throw new IllegalStateException();
    }
}
