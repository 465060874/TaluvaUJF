package engine;

import data.BuildingType;
import data.PlayerColor;

public class Player {

    private final PlayerHandler playerHandler;
    private final PlayerColor color;
    private final int[] buildings;

    public Player(PlayerHandler playerHandler, PlayerColor color) {
        this.playerHandler = playerHandler;
        this.color = color;
        this.buildings = new int[BuildingType.values().length - 1];
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getBuildingCount(BuildingType type) {
        return buildings[type.ordinal() - 1];
    }
}
