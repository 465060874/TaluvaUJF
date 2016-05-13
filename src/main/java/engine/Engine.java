package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.Coords;
import map.Map;
import map.Orientation;
import map.Village;

import java.util.List;

public interface Engine {

    void registerObserver(EngineObserver observer);

    void unregisterObserver(EngineObserver observer);

    Map getMap();

    TileStack getStack();

    List<Turn> getTurnsFromFirst();

    Iterable<Turn> getTurnsFromCurrent();

    boolean canPlaceTileOnVolcano(VolcanoTile tile, Coords coords, Orientation orientation);

    void placeTileOnVolcano(VolcanoTile tile, Coords coords, Orientation orientation);

    boolean canPlaceTileOnSea(VolcanoTile tile, Coords coords, Orientation orientation);

    void placeTileOnSea(VolcanoTile tile, Coords coords, Orientation orientation);

    boolean canBuild(BuildingType buildingType, Coords coords);

    void build(BuildingType buildingType, Coords coords);

    boolean canExpandVillage(Village village, FieldType fieldType);

    void expandVillage(Village village, FieldType fieldType);
}
