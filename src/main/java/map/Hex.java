package map;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Encapsulation des coordonnées d'un hexagone
 * Le système de coordonnées choisi est le système
 * dit de coordonnées axiales
 */
public class Hex {

    public static Hex at(int line, int diag) {
        return new Hex(line, diag);
    }

    private final int line;
    private final int diag;

    private Hex(int line, int diag) {
        this.line = line;
        this.diag = diag;
    }

    public Hex getNeighbor(Neighbor neighbor) {
        return new Hex(
                line + neighbor.lineOffset,
                diag + neighbor.diagOffset);
    }

    public Set<Hex> getNeighborhood() {
        return ImmutableSet.of(
                getNeighbor(Neighbor.NORTH_WEST),
                getNeighbor(Neighbor.NORTH_EAST),
                getNeighbor(Neighbor.WEST),
                getNeighbor(Neighbor.EAST),
                getNeighbor(Neighbor.SOUTH_WEST),
                getNeighbor(Neighbor.SOUTH_EAST));
    }

    public Hex getLeftNeighbor(Orientation orientation) {
        return getNeighbor(Neighbor.leftOf(orientation));
    }

    public Hex getRightNeighbor(Orientation orientation) {
        return getNeighbor(Neighbor.rightOf(orientation));
    }

    public int getLine() {
        return line;
    }

    public int getDiag() {
        return diag;
    }

    @Override
    public int hashCode() {
        return 17*17 + 17 * line + diag;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Hex)) {
            return false;
        }

        Hex other = (Hex) obj;
        return (line == other.line && diag == other.diag);
    }

    @Override
    public String toString() {
        return "Hex(" + line + ", " + diag + ")";
    }
}

