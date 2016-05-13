package engine;

import data.PlayerColor;

public class Turn {

    private final Player player;
    private final PlayerColor color;
    private final Buildings buildings;

    public Turn(Player player, PlayerColor color) {
        this.player = player;
        this.color = color;
        this.buildings = null;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerColor getColor() {
        return color;
    }

    public Buildings getBuildings() {
        return buildings;
    }
}
