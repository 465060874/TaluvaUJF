package map;

import data.BuildingType;
import data.PlayerColor;

import static com.google.common.base.Preconditions.*;

/**
 * Représente une construction sur une case
 *
 * Les instances de cette classes peuvent être vues
 * comme le produit cartésien des enums BuildingType et PlayerColor.
 * A l'exception de BuildingType.NONE pour lequelle la couleur n'a
 * pas de sens.
 *
 * Les 13 instances :
 *    - {BuildingType.NONE} x {null}
 *    - (BuildingType.* \ BuildingType.NONE) x PlayerColor.*
 * sont précréées et peuvent être recuperées
 * avec la méthode Building.of(BuildingType, PlayerColor)
 */
public class Building {

    public static Building none() {
        return values[0];
    }

    public static Building of(BuildingType type, PlayerColor color) {
        if (type == BuildingType.NONE) {
            checkArgument(color == null);
            return values[0];
        }
        else {
            return values[indexOf(checkNotNull(type), checkNotNull(color))];
        }
    }

    private final BuildingType type;
    private final PlayerColor color;

    private Building(BuildingType type, PlayerColor color) {
        this.type = type;
        this.color = color;
    }

    public BuildingType getType() {
        return type;
    }

    public PlayerColor getColor() {
        checkState(type != BuildingType.NONE, "Can't have a color with BuildingType.NONE");
        return color;
    }

    public int getCount(int level) {
        if (type == BuildingType.NONE) {
            return 0;
        }
        else if (type == BuildingType.HUT) {
            return level;
        }
        else {
            return 1;
        }
    }

    private static final Building[] values;

    private static int indexOf(BuildingType type, PlayerColor color) {
        return 1 + (type.ordinal() - 1) * color.values().length + color.ordinal();
    }

    static {
        values = new Building[1 + (BuildingType.values().length - 1) * PlayerColor.values().length];
        values[0] = new Building(BuildingType.NONE, null);
        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            for (PlayerColor color : PlayerColor.values()) {
                values[indexOf(type, color)] = new Building(type, color);
            }
        }
    }
}
