package engine.rules;

import data.VolcanoTile;
import map.Hex;
import map.Island;
import map.Orientation;

import java.util.ArrayList;
import java.util.List;

public class SeaPlacementRules {

    public static boolean validate(Island island, VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        return isAdjacentToCoast(island, hex, rightHex, leftHex)
                && isOnSameLevel(island, hex, rightHex, leftHex, 0);
    }

    private static boolean isOnSameLevel(Island island, Hex hex, Hex rightHex, Hex leftHex, int level) {
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

    private static boolean isAdjacentToCoast(Island island, Hex hex, Hex rightHex, Hex leftHex) {
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
