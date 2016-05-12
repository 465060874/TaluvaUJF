package map;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class Coords {

    private final int l;
    private final int d;

    public Coords(int l, int d) {
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
}
