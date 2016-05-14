package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.Island;
import map.Orientation;
import map.Village;

import java.util.List;

public class EngineImpl implements Engine {

    Island island;
    TileStack stack;

    @Override
    public void registerObserver(EngineObserver observer) {

    }

    @Override
    public void unregisterObserver(EngineObserver observer) {

    }

    public Island getIsland() {
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
    public boolean canPlaceTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation) {
        return false;
    }

    @Override
    public void placeTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation) {

    }

    @Override
    public boolean canPlaceTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation) {
        return false;
    }

    @Override
    public void placeTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation) {

    }

    @Override
    public boolean canBuild(BuildingType buildingType, Hex hex) {
        return false;
    }

    @Override
    public void build(BuildingType buildingType, Hex hex) {

    }

    @Override
    public boolean canExpandVillage(Village village, FieldType fieldType) {
        return false;
    }

    @Override
    public void expandVillage(Village village, FieldType fieldType) {

    }
}
