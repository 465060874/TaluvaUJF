package engine.tilestack;

import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import data.VolcanoTile;

import java.util.Random;

/**
 * Represente la pioche du jeu
 */
public interface VolcanoTileStack {

    static VolcanoTileStack.Factory randomFactory(Iterable<VolcanoTile> tiles) {
        return new VolcanoTileStackImpl.RandomFactory(tiles);
    }

    static VolcanoTileStack.Factory predefinedFactory(Iterable<VolcanoTile> tiles) {
        return new VolcanoTileStackImpl.PredefinedFactory(tiles);
    }

    static VolcanoTileStack.Factory read(CharSource source) {
        return VolcanoTileStackIO.read(source);
    }

    void saveAll(CharSink sink);

    int size();

    boolean isEmpty();

    VolcanoTile current();

    void next();

    VolcanoTileStack copyShuffled(Random random);

    void previous();

    interface Factory {

        VolcanoTileStack create(int count, Random random);
    }
}

