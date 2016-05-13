package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.Coords;
import map.Map;
import map.Orientation;
import map.Village;

import java.util.List;

public class EngineImpl implements Engine {

    Map map;
    TileStack stack;

    @Override
    public void registerObserver(EngineObserver observer) {

    }

    @Override
    public void unregisterObserver(EngineObserver observer) {

    }

    @Override
    public Map getMap() {
        return null;
    }

    @Override
    public TileStack getStack() {
        return null;
    }

    @Override
    public List<Turn> getTurnsFromFirst() {
        return null;
    }

    @Override
    public Iterable<Turn> getTurnsFromCurrent() {
        return null;
    }

    @Override
    public boolean canPlaceTileOnVolcano(VolcanoTile tile, Coords coords, Orientation orientation) {
        return false;
    }

    @Override
    public void placeTileOnVolcano(VolcanoTile tile, Coords coords, Orientation orientation) {

    }

    @Override
    public boolean canPlaceTileOnSea(VolcanoTile tile, Coords coords, Orientation orientation) {
        return false;
    }

    @Override
    public void placeTileOnSea(VolcanoTile tile, Coords coords, Orientation orientation) {

    }

    @Override
    public boolean canBuild(BuildingType buildingType, Coords coords) {
        return false;
    }

    @Override
    public void build(BuildingType buildingType, Coords coords) {

    }

    @Override
    public boolean canExpandVillage(Village village, FieldType fieldType) {
        return false;
    }

    @Override
    public void expandVillage(Village village, FieldType fieldType) {

    }
}
