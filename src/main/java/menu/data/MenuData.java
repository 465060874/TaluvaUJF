package menu.data;

import IA.IADifficulty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.PlayerColor;
import engine.EngineBuilder;
import engine.PlayerHandler;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class MenuData {

    private MenuMode mode;

    private PlayerColor soloColor;
    private IADifficulty soloDifficulty;

    private MultiMode multiMode;

    private final ImmutableList<SavedGame> savedGames;
    private SavedGame selectedSavedGame;

    public static MenuData load() {
        return MenuDataIO.load();
    }

    MenuData(MenuMode mode,
             PlayerColor soloColor, IADifficulty soloDifficulty,
             MultiMode multiMode,
             ImmutableList<SavedGame> savedGames) {
        this.mode = mode;
        this.soloColor = soloColor;
        this.soloDifficulty = soloDifficulty;
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

    public IADifficulty getSoloDifficulty() {
        return soloDifficulty;
    }

    public void setSoloDifficulty(IADifficulty soloDifficulty) {
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

    public EngineBuilder engineBuilder(Supplier<? extends PlayerHandler.Factory> uiPlayerFactory) {
        switch (mode) {
            case SOLO: return soloEngineBuilder(uiPlayerFactory);
            case MULTIJOUEUR: return multiEngineBuilder(uiPlayerFactory);
            case CHARGER: throw new UnsupportedOperationException("Not implemented yet");
        }

        throw new IllegalStateException();
    }

    private EngineBuilder soloEngineBuilder(Supplier<? extends PlayerHandler.Factory> uiPlayerFactory) {
        return EngineBuilder.allVsAll()
                .player(soloColor, uiPlayerFactory.get())
                .player(otherColor(soloColor), soloDifficulty.create());
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

    private EngineBuilder multiEngineBuilder(Supplier<? extends PlayerHandler.Factory> uiPlayerFactory) {
        switch (multiMode) {
            case TWO_PLAYER:
                return EngineBuilder.allVsAll()
                        .player(PlayerColor.WHITE, uiPlayerFactory.get())
                        .player(PlayerColor.RED, uiPlayerFactory.get());
            case THREE_PLAYER:
                return EngineBuilder.allVsAll()
                        .player(PlayerColor.WHITE, uiPlayerFactory.get())
                        .player(PlayerColor.RED, uiPlayerFactory.get())
                        .player(PlayerColor.YELLOW, uiPlayerFactory.get());
            case FOUR_PLAYER:
                return EngineBuilder.allVsAll()
                        .player(PlayerColor.WHITE, uiPlayerFactory.get())
                        .player(PlayerColor.RED, uiPlayerFactory.get())
                        .player(PlayerColor.YELLOW, uiPlayerFactory.get())
                        .player(PlayerColor.BROWN, uiPlayerFactory.get());
            case TEAM_VS_TEAM:
                return EngineBuilder.teamVsTeam()
                        .team(PlayerColor.BROWN, PlayerColor.WHITE, uiPlayerFactory.get())
                        .team(PlayerColor.RED, PlayerColor.YELLOW, uiPlayerFactory.get());
        }

        throw new IllegalStateException();
    }
}