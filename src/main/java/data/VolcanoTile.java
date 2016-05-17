package data;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represente une tuile volcan, en convention volcan en haut
 */
public class VolcanoTile {

    private final FieldType left;
    private final FieldType right;

    public VolcanoTile(FieldType left, FieldType right) {
        checkArgument(left.isBuildable() && right.isBuildable(),
                "VolcanoTile left and right FieldType must be buildable");

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
