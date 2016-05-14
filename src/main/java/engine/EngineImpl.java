package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.*;

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
        // On vérifie que la tuile sous le volcan est bien un volcan avec une orientation différente
        if (island.getField(hex).getType().isBuildable() || island.getField(hex).getOrientation() == orientation) {
            return false;
        }
        
        Hex rightHex = hex.getRightNeibor(orientation);
        Hex leftHex = hex.getLeftNeibor(orientation);

        if (! isOnSameLevelRule(hex, rightHex, leftHex)) return false;

        if (! isFreeOfIndestructibleBuildingRule(hex, rightHex, leftHex)) return false;

        return true;
    }

    @Override
    public void placeTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation) {

        island.putTile(tile, hex, orientation);

    }

    @Override
    public boolean canPlaceTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex rightHex = hex.getRightNeibor(orientation);
        Hex leftHex = hex.getLeftNeibor(orientation);

        Field rightField = island.getField(rightHex);
        Field leftField = island.getField(leftHex);

        // On vérifie la cohérence de niveau des îles de la tuile
        if (! isOnSameLevelRule(hex, rightHex, leftHex, 0)) return false;


        // On vérifie qu'il n'y a pas de construction non destructible
        if (! isFreeOfIndestructibleBuildingRule(hex, rightHex, leftHex)) return false;

        return true;
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

    private boolean isOnSameLevelRule(Hex hex, Hex rightHex, Hex leftHex) {
        int[] volcanoTileLevels = new int[]{island.getField(hex).getLevel(),
                island.getField(rightHex).getLevel(),
                island.getField(leftHex).getLevel()};


        int level = volcanoTileLevels[0];
        for (int i = 1; i < volcanoTileLevels.length; i++) {
            if (volcanoTileLevels[i] != level) {
                return false;
            }
        }
        return true;
    }

    private boolean isOnSameLevelRule(Hex hex, Hex rightHex, Hex leftHex, int level) {
        int[] volcanoTileLevels = new int[]{island.getField(hex).getLevel(),
                island.getField(rightHex).getLevel(),
                island.getField(leftHex).getLevel()};

        for (int i = 0; i < volcanoTileLevels.length; i++) {
            if (volcanoTileLevels[i] != level) {
                return false;
            }
        }
        return true;
    }

    private boolean isFreeOfIndestructibleBuildingRule(Hex hex, Hex rightHex, Hex leftHex) {
        FieldBuilding[] buildings = new FieldBuilding[]{island.getField(hex).getBuilding(),
                island.getField(rightHex).getBuilding(),
                island.getField(leftHex).getBuilding()};

        for (FieldBuilding building : buildings) {
            building.getColor();
        }

        //TODO

        return true;
    }

}
