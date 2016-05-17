package engine;

import data.BuildingType;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import map.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

class EngineImpl implements Engine {

    private final List<EngineObserver> observers;
    private final Island island;

    EngineImpl(Island island) {
        this.observers = new ArrayList<>();
        this.island = island;
    }

    @Override
    public void registerObserver(EngineObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(EngineObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Gamemode getGamemode() {
        return null;
    }

    public Island getIsland() {
        return null;
    }

    @Override
    public TileStack getStack() {
        return null;
    }

    @Override
    public List<Player> getPlayersFromFirst() {
        return null;
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public Iterable<Player> getPlayersFromCurrent() {
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
        observers.forEach(o -> o.onPlaceTileOnVolcano(hex, orientation));
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
        observers.forEach(o -> o.onPlaceTileOnSea(hex, orientation));
    }

    @Override
    public boolean canBuild(BuildingType type, Hex hex) {
        checkArgument(type != BuildingType.NONE);

        // Le terrain est constructible et n'est pas au niveau de la mer
        final Field field = island.getField(hex);
        if (field == Field.SEA
                || !field.getType().isBuildable()
                || field.getBuilding().getType() != BuildingType.NONE) {
            return false;
        }

        PlayerColor color = getCurrentPlayer().getColor();
        if (type == BuildingType.TEMPLE) {
            boolean villageFound = false;
            for (Hex neighbor : hex.getNeighborhood()) {
                FieldBuilding neighborBuilding = island.getField(neighbor).getBuilding();
                if (neighborBuilding.getType() != BuildingType.NONE
                        && neighborBuilding.getColor() == color) {
                    final Village village = island.getVillage(hex);
                    if (!village.hasTemple() && village.getFieldSize() > 2) {
                        villageFound = true;
                    }
                }
            }

            if (!villageFound) {
                return false;
            }
        }
        else if (type == BuildingType.TOWER) {
            if (field.getLevel() < 3) {
                return false;
            }

            boolean villageFound = false;
            for (Hex neighbor : hex.getNeighborhood()) {
                FieldBuilding neighborBuilding = island.getField(neighbor).getBuilding();
                if (neighborBuilding.getType() != BuildingType.NONE
                        && neighborBuilding.getColor() == color) {
                    final Village village = island.getVillage(hex);
                    if (village.hasTower()) {
                        villageFound = true;
                    }
                }
            }

            if (!villageFound) {
                return false;
            }
        }
        else if (type == BuildingType.HUT) {
            if (field.getLevel() != 1) {
                return false;
            }
        }

        return getCurrentPlayer().getBuildingCount(type) >= 1;
    }

    @Override
    public void build(BuildingType type, Hex hex) {
        island.putBuilding(type, hex, getCurrentPlayer().getColor());
        observers.forEach(o -> o.onBuild(type, hex));
    }

    @Override
    public boolean canExpandVillage(Village village, FieldType fieldType) {
        List<Hex> expansion = village.getExpandableHexes().get(fieldType);
        if (expansion.isEmpty()) {
            return false;
        }

        int hutsCount = 0;
        for (Hex hex : expansion) {
            hutsCount += island.getField(hex).getLevel();
        }

        return hutsCount <= getCurrentPlayer().getBuildingCount(BuildingType.HUT);
    }

    @Override
    public void expandVillage(Village village, FieldType fieldType) {
        PlayerColor color = getCurrentPlayer().getColor();

        for (Hex hex : village.getExpandableHexes().get(fieldType)) {
            island.putBuilding(BuildingType.HUT, hex, color);
        }

        observers.forEach(o -> o.onExpand(village, fieldType));
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

    private boolean isFreeOfIndestructibleBuildingRule(Hex rightHex, Hex leftHex) {
        FieldBuilding leftBuilding = island.getField(leftHex).getBuilding();
        FieldBuilding rightBuilding = island.getField(rightHex).getBuilding();

        if (leftBuilding.getType() == BuildingType.NONE
                && rightBuilding.getType() == BuildingType.NONE) {
            return true;
        }

        if (!leftBuilding.getType().isDestructible()
                || !rightBuilding.getType().isDestructible()) {
            return false;
        }

        if (leftBuilding.getType() == BuildingType.NONE) {
            Village rightVillage = island.getVillage(rightHex);
            return rightVillage.getFieldSize() > 1;
        }
        else if (rightBuilding.getType() == BuildingType.NONE) {
            Village leftVillage = island.getVillage(leftHex);
            return leftVillage.getFieldSize() > 1;
        }
        else if (leftBuilding.getType() != BuildingType.NONE
                && rightBuilding.getType() != BuildingType.NONE
                && leftBuilding.getColor() == rightBuilding.getColor()) {
            Village village = island.getVillage(leftHex);
            return village.getFieldSize() > 2;
        }
        else {
            Village leftVillage = island.getVillage(leftHex);
            Village rightVillage = island.getVillage(rightHex);
            return leftVillage.getFieldSize() > 1
                    && rightVillage.getFieldSize() > 1;
        }
    }

    private boolean isAdjacentToCoastRule(Hex hex, Hex rightHex, Hex leftHex) {
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
