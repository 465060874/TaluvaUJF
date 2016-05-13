package engine;

import data.VolcanoTile;

public interface TileStack {

    boolean isEmpty();

    VolcanoTile next();
}
