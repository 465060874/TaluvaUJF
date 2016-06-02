package engine.tilestack;

import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import data.StandardVolcanoTiles;
import data.VolcanoTile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    List<VolcanoTile> asList();

    int size();

    boolean isEmpty();

    VolcanoTile current();

    void next();

    VolcanoTileStack copyShuffled(Random random);

    void previous();

    interface Factory {

        VolcanoTileStack create(int count, Random random);
    }

    static void main(String[] args) {
        Factory factory = VolcanoTileStack.randomFactory(StandardVolcanoTiles.LIST);
        VolcanoTileStack tileStack = factory.create(24, new Random());

        File file = new File("stack3");
        tileStack.saveAll(Files.asCharSink(file, StandardCharsets.UTF_8));
    }
}

