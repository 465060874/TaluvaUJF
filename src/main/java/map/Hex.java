package map;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.Objects;

public class Hex implements Comparable<Hex> {

    public static Hex at(int line, int diag) {
        return new Hex(line, diag);
    }

    public static Ordering<Hex> lineThenDiagOrdering() {
        return LINE_THEN_DIAG_ORDERING;
    }

    private final int line;
    private final int diag;

    private Hex(int line, int diag) {
        this.line = line;
        this.diag = diag;
    }

    public Hex getNeighbor(Neighbor neighbor) {
        return new Hex(
                line + neighbor.diffLine,
                diag + neighbor.diffDiag);
    }

    public Iterable<Hex> getNeighborhood() {
        return Iterables.transform(Neighbor.list(), this::getNeighbor);
    }

    public int getLine() {
        return line;
    }

    public int getDiag() {
        return diag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, diag);
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

    @Override
    public int compareTo(Hex o) {
        return (this.line != o.line) ? this.line - o.line : this.diag - o.diag;
    }

    public Hex getRightNeighbor(Orientation orientation) {
        if (orientation == Orientation.NORTH) {
            return this.getNeighbor(Neighbor.SOUTH_EAST);
        } else if (orientation == Orientation.NORTH_EAST) {
            return this.getNeighbor(Neighbor.SOUTH_WEST);
        } else if (orientation == Orientation.SOUTH_EAST) {
            return this.getNeighbor(Neighbor.WEST);
        } else if (orientation == Orientation.SOUTH) {
            return this.getNeighbor(Neighbor.NORTH_WEST);
        } else if (orientation == Orientation.SOUTH_WEST) {
            return this.getNeighbor(Neighbor.NORTH_EAST);
        } else if (orientation == Orientation.NORTH_WEST) {
            return this.getNeighbor(Neighbor.EAST);
        } else {
            throw new RuntimeException();
        }
    }

    public Hex getLeftNeighbor(Orientation orientation) {
        if (orientation == Orientation.NORTH) {
            return this.getNeighbor(Neighbor.SOUTH_WEST);
        } else if (orientation == Orientation.NORTH_EAST) {
            return this.getNeighbor(Neighbor.WEST);
        } else if (orientation == Orientation.SOUTH_EAST) {
            return this.getNeighbor(Neighbor.NORTH_WEST);
        } else if (orientation == Orientation.SOUTH) {
            return this.getNeighbor(Neighbor.NORTH_EAST);
        } else if (orientation == Orientation.SOUTH_WEST) {
            return this.getNeighbor(Neighbor.EAST);
        } else if (orientation == Orientation.NORTH_WEST) {
            return this.getNeighbor(Neighbor.SOUTH_EAST);
        } else {
            throw new RuntimeException();
        }
    }
}

