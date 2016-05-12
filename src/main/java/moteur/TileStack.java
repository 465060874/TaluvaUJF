package moteur;

import data.VolcanoTile;

public interface TileStack {

    boolean isEmpty();

    VolcanoTile next();
}
