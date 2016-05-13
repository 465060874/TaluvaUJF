package map;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class Coords {

    public static Coords of(int l, int d) {
        return new Coords(l, d);
    }

    private final int l;
    private final int d;

    private Coords(int l, int d) {
        this.l = l;
        this.d = d;
    }

    public Coords getNeighbor(Direction direction) {

        return new Coords(l + direction.getDl(), d + direction.getDd());
    }

    public List<Coords> getNeighbors() {
        ImmutableList.Builder<Coords> builder = ImmutableList.builder();
        for (Direction direction : Direction.values()) {
            builder.add(new Coords(l + direction.getDl(), d + direction.getDd()));
        }

        return builder.build();
    }

    int getL() {
        return l;
    }

    int getD() {
        return d;
    }

    @Override
    public boolean equals (Object obj) {
        if (!(obj instanceof Coords)) {
            return false;
        }

        Coords other = (Coords) obj;
        return (l == other.l && d == other.d);
    }

    @Override
    public String toString() {
        return "Coords(" + l + ", " + d + ")";
    }
}

