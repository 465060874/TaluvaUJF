package engine.tilestack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import data.VolcanoTile;
import engine.Engine;
import engine.EngineStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

class VolcanoTileStackImpl implements VolcanoTileStack {

    private static final int TILES_PER_PLAYER = 12;

    private final Engine engine;
    private final ImmutableList<VolcanoTile> tiles;

    VolcanoTileStackImpl(Engine engine, ImmutableList<VolcanoTile> tiles) {
        this.engine = engine;
        this.tiles = tiles;
    }

    @Override
    public List<VolcanoTile> asList() {
        return tiles;
    }

    private int index() {
        if (engine.getStatus() == EngineStatus.PENDING_START) {
            return 0;
        }

        return engine.getStatus().getStep() == EngineStatus.TurnStep.TILE
                ? engine.getStatus().getTurn()
                : engine.getStatus().getTurn() + 1;
    }

    @Override
    public int size() {
        return tiles.size() - index();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public VolcanoTile current() {
        return tiles.get(index());
    }

    @Override
    public VolcanoTileStack copyShuffled(Engine engine) {
        List<VolcanoTile> copyTiles = new ArrayList<>();
        copyTiles.addAll(tiles);
        Collections.shuffle(copyTiles.subList(index() + 1, copyTiles.size()), engine.getRandom());
        return new VolcanoTileStackImpl(engine, ImmutableList.copyOf(copyTiles));
    }

    static class RandomFactory implements Factory {

        private final List<VolcanoTile> roulette;

        RandomFactory(Iterable<VolcanoTile> tiles) {
            this.roulette = new ArrayList<>();
            Iterables.addAll(roulette, tiles);
        }

        @Override
        public VolcanoTileStack create(Engine engine) {
            int count = engine.getPlayers().size() * TILES_PER_PLAYER;
            checkState(count <= roulette.size(),
                    "Insufficient number of tiles (" + roulette.size() +
                            "), expected " + count + " at least");
            Collections.shuffle(roulette, engine.getRandom());

            return new VolcanoTileStackImpl(engine, ImmutableList.copyOf(roulette.subList(0, count)));
        }
    }
}
