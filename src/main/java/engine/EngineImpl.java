package engine;

import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import map.*;

import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements Engine {

    Island island;
    TileStack stack;

    public EngineImpl(Island island) {
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

        if (! isOnSameLevelRule(hex, rightHex, leftHex)) return false;
        if (! isFreeOfIndestructibleBuildingRule(rightHex, leftHex)) return false;

        return true;
    }

    @Override
    public void placeTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation) {
        island.putTile(tile, hex, orientation);
    }

    @Override
    public boolean canPlaceTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        if (! isAdjacentToCoastRule(hex, rightHex, leftHex)) return false;
        if (! isOnSameLevelRule(hex, rightHex, leftHex, 0)) return false;

        return true;
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

    boolean isOnSameLevelRule(Hex hex, Hex rightHex, Hex leftHex, int level) {
        int[] volcanoTileLevels = new int[]{island.getField(hex).getLevel(),
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
        FieldBuilding[] buildings = new FieldBuilding[]{
                island.getField(rightHex).getBuilding(),
                island.getField(leftHex).getBuilding()};

        boolean anyBuilding = true;
        for (FieldBuilding building : buildings) {
            anyBuilding = anyBuilding && (building.getType() == BuildingType.NONE);
        }

        if (anyBuilding) return true;

        for (FieldBuilding building : buildings) {
            final BuildingType type = building.getType();
            if (type == BuildingType.TEMPLE || type == BuildingType.TOWER) {
                return false;
            }
        }

        // A optimiser de façon a comparer uniquement avec le village des constructions concernés
        // et non tout les villages de la couleur des construcions consernés
        for (FieldBuilding building : buildings) {
            final PlayerColor color = building.getColor();
            final Iterable<Village> villages = island.getVillages(color);
            for (Village village : villages) {
                int villageFieldSize = village.getFieldSize();
                if (villageFieldSize > buildings.length) {
                    continue;
                }

                final Iterable<Hex> hexes = village.getHexes();
                if (villageFieldSize == 2) {
                    for (Hex hex : hexes) {
                        if (hex.compareTo(rightHex) == 0
                                && hex.compareTo(leftHex) == 0) {
                            return false;
                        }
                    }
                } else {
                    for (Hex hex : hexes) {
                        if (hex.compareTo(rightHex) == 0
                                || hex.compareTo(leftHex) == 0) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
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
                    if (hexCoast.compareTo(hexNeighbor) == 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
