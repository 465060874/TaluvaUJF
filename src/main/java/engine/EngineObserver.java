package engine;

import data.BuildingType;
import data.FieldType;
import map.Coords;
import map.Orientation;
import map.Village;

public interface EngineObserver {

    void onPlaceTileOnVolcano(Coords coords, Orientation orientation);

    void onPlaceTileOnSea(Coords coords, Orientation orientation);

    void onBuild(BuildingType buildingType, Coords coords);

    void onExpand(Village village, FieldType fieldType);

    void beforeTurnStart();
}
