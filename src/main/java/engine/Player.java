package engine;

import data.PlayerColor;

public class Player {

    private final PlayerHandler playerHandler;
    private final PlayerColor color;
    private final Buildings buildings;

    public Player(PlayerHandler playerHandler, PlayerColor color) {
        this.playerHandler = playerHandler;
        this.color = color;
        this.buildings = null;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public PlayerColor getColor() {
        return color;
    }

    public Buildings getBuildings() {
        return buildings;
    }
}
