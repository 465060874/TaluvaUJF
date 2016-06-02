package engine.tilestack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.CharSink;
import data.VolcanoTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkState;

class VolcanoTileStackImpl implements VolcanoTileStack {

    private final ImmutableList<VolcanoTile> tiles;
    private int index;

    private VolcanoTileStackImpl(ImmutableList<VolcanoTile> tiles) {
        this.tiles = tiles;
        this.index = -1;
    }

    private VolcanoTileStackImpl(ImmutableList<VolcanoTile> tiles, int index) {
        this.tiles = tiles;
        this.index = index;
    }

    @Override
    public void saveAll(CharSink sink) {
        VolcanoTileStackIO.write(sink, tiles);
    }

    @Override
    public List<VolcanoTile> asList() {
        return tiles;
    }

    @Override
    public int size() {
        return tiles.size() - index;
    }

    @Override
    public boolean isEmpty() {
        return index >= tiles.size();
    }

    @Override
    public VolcanoTile current() {
        return index >= 0 ? tiles.get(index) : null;
    }

    @Override
    public void next() {
        index++;
    }

    @Override
    public void previous() {
        index--;
    }

    @Override
    public VolcanoTileStack copyShuffled(Random random) {
        List<VolcanoTile> copyTiles = new ArrayList<>();
        copyTiles.addAll(tiles);
        Collections.shuffle(copyTiles.subList(index + 1, copyTiles.size()), random);
        return new VolcanoTileStackImpl(ImmutableList.copyOf(copyTiles), index);
    }

    static class RandomFactory implements Factory {

        private final List<VolcanoTile> roulette;

        RandomFactory(Iterable<VolcanoTile> tiles) {
            this.roulette = new ArrayList<>();
            Iterables.addAll(roulette, tiles);
        }

        @Override
        public VolcanoTileStack create(int count, Random random) {
            checkState(count <= roulette.size(),
                    "Insufficient number of tiles (" + roulette.size() +
                            "), expected " + count + " at least");
            Collections.shuffle(roulette, random);

            return new VolcanoTileStackImpl(ImmutableList.copyOf(roulette.subList(0, count)));
        }
    }

    static class PredefinedFactory implements Factory {

        private final ImmutableList<VolcanoTile> tiles;

        PredefinedFactory(Iterable<VolcanoTile> tiles) {
            this.tiles = ImmutableList.copyOf(tiles);
        }

        @Override
        public VolcanoTileStack create(int count, Random random) {
            checkState(count <= tiles.size(),
                    "Insufficient number of tiles (" + tiles.size() +
                            "), expected " + count + " at least");
            return new VolcanoTileStackImpl(tiles.subList(0, count));
        }
    }
}
