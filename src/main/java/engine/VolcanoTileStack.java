package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.VolcanoTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkState;

/**
 * Represente la pioche du jeu
 */
public interface VolcanoTileStack {

    static VolcanoTileStack.Factory randomFactory(Iterable<VolcanoTile> tiles) {
        return new RandomVolcanoTileStack.Factory(tiles);
    }

    int size();

    boolean isEmpty();

    VolcanoTile current();

    void next();

    VolcanoTileStack copy(Random random);

    interface Factory {

        VolcanoTileStack create(Gamemode gamemode, Random random);
    }
}

class RandomVolcanoTileStack implements VolcanoTileStack {

    private final ImmutableList<VolcanoTile> tiles;
    private int index;

    private RandomVolcanoTileStack(ImmutableList<VolcanoTile> tiles) {
        this.tiles = tiles;
        this.index = 0;
    }

    private RandomVolcanoTileStack(ImmutableList<VolcanoTile> tiles, int index) {
        this.tiles = tiles;
        this.index = index;
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
        return tiles.get(index);
    }

    @Override
    public void next() {
        index++;
    }

    @Override
    public VolcanoTileStack copy(Random random) {
        List<VolcanoTile> copyTiles = new ArrayList<>();
        copyTiles.addAll(tiles);
        Collections.shuffle(copyTiles.subList(index + 1, copyTiles.size()));
        return new RandomVolcanoTileStack(ImmutableList.copyOf(copyTiles), index);
    }

    static class Factory implements VolcanoTileStack.Factory {

        private final List<VolcanoTile> roulette;

        Factory(Iterable<VolcanoTile> tiles) {
            this.roulette = new ArrayList<>();
            Iterables.addAll(roulette, tiles);
        }

        @Override
        public VolcanoTileStack create(Gamemode gamemode, Random random) {
            checkState(gamemode.getTilesCount() <= roulette.size(),
                    "Insufficient number of tiles (" + gamemode.getTilesCount() +
                            "), expected " + gamemode.getTilesCount() + " at least");
            Collections.shuffle(roulette, random);

            return new RandomVolcanoTileStack(ImmutableList.copyOf(roulette.subList(0, gamemode.getTilesCount())));
        }
    }
}