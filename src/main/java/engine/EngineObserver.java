package engine;

import data.BuildingType;
import data.FieldType;
import map.Hex;
import map.Orientation;
import map.Village;

public interface EngineObserver {

    void beforeTurnStart();

    void onPlaceTileOnVolcano(Hex hex, Orientation orientation);

    void onPlaceTileOnSea(Hex hex, Orientation orientation);

    void onBuild(BuildingType buildingType, Hex hex);

    void onExpand(Village village, FieldType fieldType);
}
