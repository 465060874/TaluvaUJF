package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.VolcanoTile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Represente la pioche du jeu
 */
public interface TileStack {

    static TileStack createRandom(Iterable<VolcanoTile> tiles, Gamemode gamemode, Random random) {
        return new RandomTileStack(tiles, gamemode, random);
    }

    int size();

    boolean isEmpty();

    VolcanoTile current();

    void next();
}

class RandomTileStack implements TileStack {

    private final ImmutableList<VolcanoTile> tiles;
    private int index;

    RandomTileStack(Iterable<VolcanoTile> tiles, Gamemode gamemode, Random random) {
        ArrayList<VolcanoTile> roulette = new ArrayList<>();
        Iterables.addAll(roulette, tiles);
        Collections.shuffle(roulette, random);

        this.tiles = ImmutableList.copyOf(roulette.subList(0, gamemode.getTilesCount()));
        this.index = 0;
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
}