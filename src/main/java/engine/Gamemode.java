package engine;

public enum Gamemode {

    TwoPlayer(48, 2),

    ThreePlayer(48, 3),

    FourPlayer(48, 4),

    TeamVsTeam(48, 4);

    private final int playerCount;
    private final int tilesCount;

    Gamemode(int tilesCount, int playerCount) {
        this.tilesCount = tilesCount;
        this.playerCount = playerCount;
    }

    public int getTilesCount() {
        return tilesCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
