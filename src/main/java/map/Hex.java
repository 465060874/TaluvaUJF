package map;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Encapsulation des coordonnées d'un hexagone
 * Le système de coordonnées choisi est le système
 * dit de coordonnées axiales
 */
public class Hex {

    // On conserver les instances allouées (à la demande)
    // dans ce tableau, le différence en terme de vitesse
    // d'éxecution est infime, mais celle de la consommation
    // mémoire est notable.
    private static final Hex[] HEXES = new Hex[40000];

    public static Hex at(int line, int diag) {
        int index = (line + 100) * 200 + (diag + 100);
        Hex hex = HEXES[index];
        return hex == null
                ? HEXES[index] = new Hex(line, diag)
                : hex;
    }

    private final int line;
    private final int diag;
    private final int hash;

    private Hex(int line, int diag) {
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

    // Equals is identity
    // @Override
    // public boolean equals(Object obj) {
    //     return this == obj;
    // }

    @Override
    public String toString() {
        return "Hex(" + line + ", " + diag + ")";
    }
}

