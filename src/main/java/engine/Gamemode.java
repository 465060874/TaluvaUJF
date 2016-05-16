package engine;

public enum Gamemode {

    TwoPlayer(48, 2, 2),

    ThreePlayer(48, 3, 3),

    FourPlayer(48, 4, 4),

    TeamVsTeam(48, 2, 4);

    private final int playerCount;
    private final int colorCount;
    private final int tilesCount;

    Gamemode(int tilesCount, int playerCount, int colorCount) {
        this.tilesCount = tilesCount;
        this.playerCount = playerCount;
        this.colorCount = colorCount;
    }

    public int getTilesCount() {
        return tilesCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getColorCount() {
        return colorCount;
    }
}
