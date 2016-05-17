package map;

import data.BuildingType;
import data.FieldType;
import static com.google.common.base.Preconditions.*;

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
    private final FieldBuilding building;

    private Field(int level, FieldType type, Orientation orientation) {
        this(level, type, orientation, FieldBuilding.of(BuildingType.NONE, null));
    }

    private Field(int level, FieldType type, Orientation orientation, FieldBuilding building) {
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

    public FieldBuilding getBuilding() {
        return building;
    }

    public Field withBuilding(FieldBuilding building) {
        return new Field(level, type, orientation, building);
    }

    public boolean isSeaLevel() {
        return this.level == 0;
    }
}
