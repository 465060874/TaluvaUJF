package data;

/**
 * Represente une tuile volcan, en convention volcan en haut
 */
public class VolcanoTile {

    private final FieldType left;
    private final FieldType right;

    public VolcanoTile(FieldType left, FieldType right) {
        this.left = left;
        this.right = right;
        if (!(left.isBuildable() && right.isBuildable())) {
            throw new IllegalArgumentException("VolcanoTile left and right FieldType must be buildable");
        }
    }

    public FieldType getLeft() {
        return left;
    }

    public FieldType getRight() {
        return right;
    }
}
