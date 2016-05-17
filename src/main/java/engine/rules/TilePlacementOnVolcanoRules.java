package engine.rules;

import data.BuildingType;
import data.VolcanoTile;
import engine.Engine;
import map.*;

public class TilePlacementOnVolcanoRules {

    public static boolean validate(Island island, VolcanoTile tile, Hex hex, Orientation orientation) {
        // On vérifie que la tuile sous le volcan est bien un volcan avec une orientation différente
        if (island.getField(hex).getType().isBuildable() || island.getField(hex).getOrientation() == orientation) {
            return false;
        }

        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        return isOnSameLevel(island, hex, rightHex, leftHex)
                && isFreeOfIndestructibleBuilding(island, rightHex, leftHex);
    }

    static boolean isOnSameLevel(Island island, Hex hex, Hex rightHex, Hex leftHex) {
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

    static boolean isFreeOfIndestructibleBuilding(Island island, Hex rightHex, Hex leftHex) {
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
            return rightVillage.getHexSize() > 1;
        }
        else if (rightBuilding.getType() == BuildingType.NONE) {
            Village leftVillage = island.getVillage(leftHex);
            return leftVillage.getHexSize() > 1;
        }
        else if (leftBuilding.getType() != BuildingType.NONE
                && rightBuilding.getType() != BuildingType.NONE
                && leftBuilding.getColor() == rightBuilding.getColor()) {
            Village village = island.getVillage(leftHex);
            return village.getHexSize() > 2;
        }
        else {
            Village leftVillage = island.getVillage(leftHex);
            Village rightVillage = island.getVillage(rightHex);
            return leftVillage.getHexSize() > 1
                    && rightVillage.getHexSize() > 1;
        }
    }
}
