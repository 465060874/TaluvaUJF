package map;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public class Hex {

    public static Hex at(int line, int diag) {
        return new Hex(line, diag);
    }

    public static Ordering<Hex> lineThenDiagOrdering() {
        return LINE_THEN_DIAG_ORDERING;
    }

    final int line;
    final int diag;

    private Hex(int line, int diag) {
        this.line = line;
        this.diag = diag;
    }

    public Hex getNeighbor(Neighbor neighbor) {
        return new Hex(
                line + neighbor.lineOffset,
                diag + neighbor.diagOffset);
    }

    public Iterable<Hex> getNeighborhood() {
        return Iterables.transform(Neighbor.list(), this::getNeighbor);
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
        return 961 + 31 * line + diag;
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

    private static final Ordering<Hex> LINE_THEN_DIAG_ORDERING = new Ordering<Hex>() {
        @Override
        public int compare(Hex left, Hex right) {
            return ComparisonChain.start()
                    .compare(left.line, right.line)
                    .compare(left.diag, right.diag)
                    .result();
        }
    };
}

