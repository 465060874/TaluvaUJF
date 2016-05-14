package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.Island;
import map.Orientation;
import map.Village;

import java.util.List;

public interface Engine {

    void registerObserver(EngineObserver observer);

    void unregisterObserver(EngineObserver observer);

    Island getIsland();

    TileStack getStack();

    List<Turn> getTurnsFromFirst();

    Iterable<Turn> getTurnsFromCurrent();

    boolean canPlaceTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation);

    void placeTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation);

    boolean canPlaceTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation);

    void placeTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation);

    boolean canBuild(BuildingType buildingType, Hex hex);

    void build(BuildingType buildingType, Hex hex);

    boolean canExpandVillage(Village village, FieldType fieldType);

    void expandVillage(Village village, FieldType fieldType);
}
