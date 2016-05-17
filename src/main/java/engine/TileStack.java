package engine;

import data.VolcanoTile;

/**
 * Represente la pioche du jeu
 */
public interface TileStack {

    boolean isEmpty();

    VolcanoTile next();
}
