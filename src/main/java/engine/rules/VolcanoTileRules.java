package engine.rules;

import data.BuildingType;
import data.FieldType;
import map.*;

public class VolcanoTileRules {

    public static Problem validate(Island island, Hex hex, Orientation orientation) {
        // On vérifie que la tuile sous le volcan est bien un volcan avec une orientation différente
        if (island.getField(hex).getType() != FieldType.VOLCANO) {
            return Problem.NOT_ON_VOLCANO;
        }

        if (island.getField(hex).getOrientation() == orientation) {
            return Problem.SAME_VOLCANO_ORIENTATION;
        }

        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        return checkOnSameLevel(island, hex, rightHex, leftHex)
                .and(() -> checkFreeOfIndestructibleBuilding(island, rightHex, leftHex));
    }

    private static Problem checkOnSameLevel(Island island, Hex hex, Hex rightHex, Hex leftHex) {
        int[] volcanoTileLevels = new int[]{
                island.getField(hex).getLevel(),
                island.getField(rightHex).getLevel(),
                island.getField(leftHex).getLevel()};

        int level = volcanoTileLevels[0];
        for (int i = 1; i < volcanoTileLevels.length; i++) {
            if (volcanoTileLevels[i] != level) {
                return Problem.NOT_ON_SAME_LEVEL;
            }
        }

        return Problem.NONE;
    }

    private static Problem checkFreeOfIndestructibleBuilding(Island island, Hex rightHex, Hex leftHex) {
        Building leftBuilding = island.getField(leftHex).getBuilding();
        Building rightBuilding = island.getField(rightHex).getBuilding();

        if (leftBuilding.getType() == BuildingType.NONE
                && rightBuilding.getType() == BuildingType.NONE) {
            return Problem.NONE;
        }

        if (!leftBuilding.getType().isDestructible()
                || !rightBuilding.getType().isDestructible()) {
            return Problem.CANT_DESTROY_TOWER_OR_TEMPLE;
        }

        if (leftBuilding.getType() == BuildingType.NONE) {
            Village rightVillage = island.getVillage(rightHex);
            if (rightVillage.getHexes().size() == 1) {
                return Problem.CANT_DESTROY_VILLAGE;
            }
        }
        else if (rightBuilding.getType() == BuildingType.NONE) {
            Village leftVillage = island.getVillage(leftHex);
            if (leftVillage.getHexes().size() == 1) {
                return Problem.CANT_DESTROY_VILLAGE;
            }
        }
        else if (leftBuilding.getType() != BuildingType.NONE
                && rightBuilding.getType() != BuildingType.NONE
                && leftBuilding.getColor() == rightBuilding.getColor()) {
            Village village = island.getVillage(leftHex);
            if (village.getHexes().size() == 2) {
                return Problem.CANT_DESTROY_VILLAGE;
            }
        }
        else {
            Village leftVillage = island.getVillage(leftHex);
            Village rightVillage = island.getVillage(rightHex);
            if (leftVillage.getHexes().size() == 1 || rightVillage.getHexes().size() == 1) {
                return Problem.CANT_DESTROY_VILLAGE;
            }
        }

        return Problem.NONE;
    }
}
