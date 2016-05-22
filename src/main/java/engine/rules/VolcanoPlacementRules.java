package engine.rules;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.*;

public class VolcanoPlacementRules {

    public static Problems<PlacementProblem>
            validate(Island island, VolcanoTile tile, Hex hex, Orientation orientation) {
        // On vérifie que la tuile sous le volcan est bien un volcan avec une orientation différente
        if (island.getField(hex).getType() != FieldType.VOLCANO) {
            return Problems.of(PlacementProblem.NOT_ON_VOLCANO);
        }

        if (island.getField(hex).getOrientation() == orientation) {
            return Problems.of(PlacementProblem.SAME_VOLCANO_ORIENTATION);
        }

        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        Problems<PlacementProblem> problems = Problems.create(PlacementProblem.class);
        checkOnSameLevel(island, hex, rightHex, leftHex, problems);
        checkFreeOfIndestructibleBuilding(island, rightHex, leftHex, problems);
        return problems;
    }

    private static void checkOnSameLevel(Island island, Hex hex, Hex rightHex, Hex leftHex,
             Problems<PlacementProblem> problems) {
        int[] volcanoTileLevels = new int[]{
                island.getField(hex).getLevel(),
                island.getField(rightHex).getLevel(),
                island.getField(leftHex).getLevel()};

        int level = volcanoTileLevels[0];
        for (int i = 1; i < volcanoTileLevels.length; i++) {
            if (volcanoTileLevels[i] != level) {
                problems.add(PlacementProblem.NOT_ON_SAME_LEVEL);
                return;
            }
        }
    }

    private static void checkFreeOfIndestructibleBuilding(Island island, Hex rightHex, Hex leftHex,
              Problems<PlacementProblem> problems) {
        FieldBuilding leftBuilding = island.getField(leftHex).getBuilding();
        FieldBuilding rightBuilding = island.getField(rightHex).getBuilding();

        if (leftBuilding.getType() == BuildingType.NONE
                && rightBuilding.getType() == BuildingType.NONE) {
            return;
        }

        if (!leftBuilding.getType().isDestructible()
                || !rightBuilding.getType().isDestructible()) {
            problems.add(PlacementProblem.CANT_DESTROY_TOWER_OR_TEMPLE);
            return;
        }

        if (leftBuilding.getType() == BuildingType.NONE) {
            Village rightVillage = island.getVillage(rightHex);
            if (rightVillage.getHexSize() == 1) {
                problems.add(PlacementProblem.CANT_DESTROY_VILLAGE);
            }
        }
        else if (rightBuilding.getType() == BuildingType.NONE) {
            Village leftVillage = island.getVillage(leftHex);
            if (leftVillage.getHexSize() == 1) {
                problems.add(PlacementProblem.CANT_DESTROY_VILLAGE);
            }
        }
        else if (leftBuilding.getType() != BuildingType.NONE
                && rightBuilding.getType() != BuildingType.NONE
                && leftBuilding.getColor() == rightBuilding.getColor()) {
            Village village = island.getVillage(leftHex);
            if (village.getHexSize() == 2) {
                problems.add(PlacementProblem.CANT_DESTROY_VILLAGE);
            }
        }
        else {
            Village leftVillage = island.getVillage(leftHex);
            Village rightVillage = island.getVillage(rightHex);
            if (leftVillage.getHexSize() == 1 || rightVillage.getHexSize() > 1) {
                problems.add(PlacementProblem.CANT_DESTROY_VILLAGE);
            }
        }
    }
}
