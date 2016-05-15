package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.*;

import java.util.ArrayList;
import java.util.List;

class EngineImpl implements Engine {

    private final Island island;

    EngineImpl(Island island) {
        this.island = island;
    }

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
        
        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        return isOnSameLevelRule(hex, rightHex, leftHex)
                && isFreeOfIndestructibleBuildingRule(rightHex, leftHex);
    }

    @Override
    public void placeTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation) {
        island.putTile(tile, hex, orientation);
    }

    @Override
    public boolean canPlaceTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        return isAdjacentToCoastRule(hex, rightHex, leftHex)
                && isOnSameLevelRule(hex, rightHex, leftHex, 0);
    }

    @Override
    public void placeTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation) {
        island.putTile(tile, hex, orientation);
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

    boolean isOnSameLevelRule(Hex hex, Hex rightHex, Hex leftHex) {
        int[] volcanoTileLevels = new int[]{
                island.getField(hex).getLevel(),
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

    boolean isOnSameLevelRule(Hex hex, Hex rightHex, Hex leftHex, int level) {
        int[] volcanoTileLevels = new int[]{
                island.getField(hex).getLevel(),
                island.getField(rightHex).getLevel(),
                island.getField(leftHex).getLevel()};

        for (int volcanoTileLevel : volcanoTileLevels) {
            if (volcanoTileLevel != level) {
                return false;
            }
        }
        return true;
    }

    boolean isFreeOfIndestructibleBuildingRule(Hex rightHex, Hex leftHex) {
        FieldBuilding leftBuilding = island.getField(leftHex).getBuilding();
        FieldBuilding rightBuilding = island.getField(rightHex).getBuilding();

        if (!leftBuilding.getType().isDestructible()
                || !rightBuilding.getType().isDestructible()) {
            return false;
        }

        if (leftBuilding.getType() != BuildingType.NONE
                && rightBuilding.getType() != BuildingType.NONE
                && leftBuilding.getColor() == rightBuilding.getColor()) {
            Village village = island.getVillage(leftHex);
            return village.getFieldSize() > 2;
        }

        Village leftVillage = island.getVillage(leftHex);
        Village rightVillage = island.getVillage(rightHex);
        return leftVillage.getFieldSize() > 1
                && rightVillage.getFieldSize() > 1;
    }

    boolean isAdjacentToCoastRule(Hex hex, Hex rightHex, Hex leftHex) {
        final Iterable<Hex> coast = island.getCoast();
        List<Iterable<Hex>> neighborhoods = new ArrayList<>();
        neighborhoods.add(hex.getNeighborhood());
        neighborhoods.add(rightHex.getNeighborhood());
        neighborhoods.add(leftHex.getNeighborhood());

        // A optimiser
        for (Hex hexCoast : coast) {
            for (Iterable<Hex> neighborhood : neighborhoods) {
                for (Hex hexNeighbor : neighborhood) {
                    if (hexCoast.equals(hexNeighbor)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
