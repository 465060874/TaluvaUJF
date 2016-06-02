package map;

import data.BuildingType;
import data.FieldType;
import data.PlayerColor;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Représente une case de la carte
 *
 * Chaque case possède une orientation, défini par la tuile volcan
 * depuis laquelle elle a été placé.
 * La carte possède une orientation implicite :
 *    - Nord vers les lignes négatives
 *    - Nord-Ouest vers les diagonales négatives
 * L'orientation de la case est défini relativement au nord de la carte
 * Par exemple pour une case volcan, si la pointe du volcan est vers le
 * nord de la carte, l'orientation de la case est NORTH.
 *
 * Les instances de Field peuvent être créé à l'aide de la méthode
 * statique Field.create(level, FieldType, Orientation)
 *
 * Les cases sont initialement créés sans batiment.
 * Pour ajouter un batiment, il suffit d'utiliser la méthode
 * fieldWithoutBuilding#withBuilding(building)
 */
public class Field {

    // Instance unique de la classe Field répresentant les Field de type MER
    // Agis comme l'élément null de la classe (cf. Null Object Pattern)
    public static Field SEA = new Field(0, null, Orientation.NORTH);

    public static Field create(int level, FieldType type, Orientation orientation) {
        checkArgument(level > 0);
        checkNotNull(type);
        checkNotNull(orientation);

        return new Field(level, type, orientation);
    }

    private final int level;
    private final FieldType type;
    private final Orientation orientation;
    private final Building building;

    private Field(int level, FieldType type, Orientation orientation) {
        this(level, type, orientation, Building.of(BuildingType.NONE, null));
    }

    private Field(int level, FieldType type, Orientation orientation, Building building) {
        this.level = level;
        this.type = type;
        this.orientation = orientation;
        this.building = building;
    }

    public int getLevel() {
        return level;
    }

    public FieldType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public boolean hasBuilding() {
        return building.getType() != BuildingType.NONE;
    }

    public boolean hasBuilding(PlayerColor color) {
        return building.getType() != BuildingType.NONE
                && building.getColor() == color;
    }

    public Building getBuilding() {
        return building;
    }

    public int getBuildingCount() {
        switch (building.getType()) {
            case NONE: return 0;
            case HUT: return level;
            case TEMPLE: return 1;
            case TOWER: return 1;
        }

        throw new IllegalStateException();
    }

    public Field withBuilding(Building building) {
        return new Field(level, type, orientation, building);
    }
}
