package data;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof VolcanoTile)) {
            return false;
        }

        VolcanoTile other = (VolcanoTile) obj;
        return this.left == other.left && this.right == other.right;
    }

    @Override
    public String toString() {
        return "VolcanoTile(" + this.left + ", " + this.right + ")";
    }
}
