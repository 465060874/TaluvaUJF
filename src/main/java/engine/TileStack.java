package engine;

import data.VolcanoTile;

/**
 * Represente la pioche du jeu
 */
public interface TileStack {

    int size();

    boolean isEmpty();

    VolcanoTile current();

    void next();
}
