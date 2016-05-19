package engine;

import data.BuildingType;
import data.PlayerColor;

/**
 * Represente un joueur, sa couleur et ses batiments restants
 */
public class Player {

    private final PlayerColor color;
    private final int[] buildings;
    private final PlayerHandler playerHandler;

    public Player(PlayerColor color, PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
        this.buildings = new int[BuildingType.values().length - 1];
        for (BuildingType type : BuildingType.values()) {
            if (type != BuildingType.NONE) {
                buildings[type.ordinal() - 1] = type.getInitialCount();
            }
        }
        this.color = color;
    }

    private Player(Player player, PlayerHandler playerHandler) {
        this.color = player.color;
        this.buildings = new int[player.buildings.length];
        System.arraycopy(player.buildings, 0, buildings, 0, buildings.length);
        this.playerHandler = playerHandler;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getBuildingCount(BuildingType type) {
        return buildings[type.ordinal() - 1];
    }

    void updateBuildingCount(BuildingType type, int count) {
        buildings[type.ordinal() - 1] = count;
    }

    public PlayerHandler getHandler() {
        return playerHandler;
    }

    public Player copyWithDummyHandler() {
        return new Player(this, PlayerHandler.dummy());
    }
}
