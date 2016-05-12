package moteur;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.Coords;
import map.Map;
import map.Orientation;
import map.Village;

import java.util.List;

public interface Moteur {

    Map getMap();

    TileStack getStack();

    List<Turn> getTurnsFromFirst();

    List<Turn> getTurnsFromCurrent();

    boolean canPlaceTileOnVolcano(VolcanoTile tile, Coords coords, Orientation orientation);

    boolean canPlaceTileOnSea(VolcanoTile tile, Coords coords, Orientation orientation);

    boolean canBuild(BuildingType buildingType, Coords coords);

    boolean canExtendVillage(Village village, FieldType fieldType);
}
