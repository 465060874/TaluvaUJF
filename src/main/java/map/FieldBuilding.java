package map;

import data.BuildingType;
import data.PlayerColor;

import static com.google.common.base.Preconditions.*;

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
