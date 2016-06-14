package map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Encapsulation des coordonnées d'un hexagone
 * Le système de coordonnées choisi est le système
 * dit de coordonnées axiales
 */
public class Hex {

    // On conserve certaines instances allouées (à la demande)
    // dans ce tableau, la différence en terme de vitesse
    // d'éxecution est infime, mais celle de la consommation
    // mémoire est notable.
    private static final int MIN_LINE = -20;
    private static final int MAX_LINE = 20;
    private static final int MIN_DIAG = -20;
    private static final int MAX_DIAG = 20;
    private static final Hex[] HEXES = new Hex[(MAX_LINE - MIN_LINE) * (MAX_DIAG - MIN_DIAG)];

    public static Hex at(int line, int diag) {
        if (line >= MIN_LINE && line < MAX_LINE
                && diag >= MIN_DIAG && diag < MAX_DIAG) {
            int index = (line - MIN_LINE) * (MAX_DIAG - MIN_DIAG) + (diag + MIN_DIAG);
            Hex hex = HEXES[index];
            return hex == null
                    ? HEXES[index] = new Hex(line, diag)
                    : hex;
        }
        else {
            return new Hex(line, diag);
        }
    }

    private final int line;
    private final int diag;
    private final int hash;

    @VisibleForTesting
    protected Hex(int line, int diag) {
        this.line = line;
        this.diag = diag;
        this.hash = 17*17 + 17 * line + diag;
    }

    public Hex getNeighbor(Neighbor neighbor) {
        return at(
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
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
         if (this == obj) {
             return true;
         }

        if (!(obj instanceof Hex)) {
            return false;
        }

        Hex other = (Hex) obj;
        return line == other.line && diag == other.diag;
    }

    @Override
    public String toString() {
        return "Hex(" + line + ", " + diag + ")";
    }
}

