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
    }

    public FieldType getLeft() {
        return left;
    }

    public FieldType getRight() {
        return right;
    }
}
