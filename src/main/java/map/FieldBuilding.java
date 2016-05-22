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
 * avec la méthode FieldBuilding.create(BuildingType, PlayerColor)
 */
public class FieldBuilding {

    public static FieldBuilding of(BuildingType type, PlayerColor color) {
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

    private FieldBuilding(BuildingType type, PlayerColor color) {
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

    private static final FieldBuilding[] values;

    private static int indexOf(BuildingType type, PlayerColor color) {
        return 1 + (type.ordinal() - 1) * color.values().length + color.ordinal();
    }

    static {
        values = new FieldBuilding[1 + (BuildingType.values().length - 1) * PlayerColor.values().length];
        values[0] = new FieldBuilding(BuildingType.NONE, null);
        for (BuildingType type : BuildingType.values()) {
            if (type == BuildingType.NONE) {
                continue;
            }

            for (PlayerColor color : PlayerColor.values()) {
                values[indexOf(type, color)] = new FieldBuilding(type, color);
            }
        }
    }
}
