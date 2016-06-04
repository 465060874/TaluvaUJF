package engine;

import data.BuildingType;
import data.PlayerColor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represente un joueur, sa couleur et ses batiments restants
 */
public class Player {

    private final PlayerColor color;
    private final int[] buildings;
    private final PlayerHandler handler;
    boolean eliminated;

    Player(PlayerColor color, PlayerHandler handler) {
        this.handler = handler;
        this.buildings = new int[BuildingType.values().length - 1];
        for (BuildingType type : BuildingType.values()) {
            if (type != BuildingType.NONE) {
                buildings[type.ordinal() - 1] = type.getInitialCount();
            }
        }
        this.color = color;
        this.eliminated = false;
    }

    private Player(Player player, PlayerHandler handler) {
        this.color = player.color;
        this.buildings = new int[player.buildings.length];
        System.arraycopy(player.buildings, 0, buildings, 0, buildings.length);
        this.handler = handler;
        this.eliminated = false;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getBuildingCount(BuildingType type) {
        return buildings[type.ordinal() - 1];
    }

    public boolean isEliminated() {
        return eliminated;
    }

    void updateBuildingCount(BuildingType type, int count) {
        checkArgument(count >= 0);
        buildings[type.ordinal() - 1] = count;
    }

    void decreaseBuildingCount(BuildingType type, int count) {
        checkArgument(buildings[type.ordinal() - 1] >= count);
        buildings[type.ordinal() - 1] -= count;
    }

    public boolean isHuman() {
        return handler.isHuman();
    }

    public PlayerHandler getHandler() {
        return handler;
    }

    public Player copy() {
        return new Player(this, handler);
    }

    public Player copyWithDummyHandler() {
        return new Player(this, PlayerHandler.dummy());
    }

    void setEliminated() {
        this.eliminated = true;
    }
}
