package engine.tilestack;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import data.StandardVolcanoTiles;
import data.VolcanoTile;
import engine.Engine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represente la pioche du jeu
 */
public interface VolcanoTileStack {

    static VolcanoTileStack.Factory randomFactory(Iterable<VolcanoTile> tiles) {
        return new VolcanoTileStackImpl.RandomFactory(tiles);
    }

    static VolcanoTileStack.Factory predefinedFactory(Iterable<VolcanoTile> tilesIt) {
        ImmutableList<VolcanoTile> tiles = ImmutableList.copyOf(tilesIt);
        return (e) -> new VolcanoTileStackImpl(e, tiles);
    }

    static VolcanoTileStack.Factory read(CharSource source) {
        return VolcanoTileStackIO.read(source);
    }

    static void save(CharSink sink, Iterable<VolcanoTile> tiles) {
        VolcanoTileStackIO.write(sink, ImmutableList.copyOf(tiles));
    }

    List<VolcanoTile> asList();

    int size();

    boolean isEmpty();

    VolcanoTile current();

    VolcanoTileStack copyShuffled(Engine engine);

    interface Factory {

        VolcanoTileStack create(Engine engine);
    }

    // Generate a new random stack for IA Battle against Group 2 !
    static void main(String[] args) {
        List<VolcanoTile> tiles = new ArrayList<>();
        tiles.addAll(StandardVolcanoTiles.LIST);
        Collections.shuffle(tiles);

        File file = new File("stack3");
        save(Files.asCharSink(file, StandardCharsets.UTF_8), tiles);
    }
}

