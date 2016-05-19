package engine.rules;

import com.google.common.collect.Iterables;
import data.VolcanoTile;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

import java.util.ArrayList;
import java.util.List;

public class SeaPlacementRules {

    public static boolean validate(Island island, VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);
        return isOnSea(island, hex, rightHex, leftHex)
                && isAdjacentToCoast(island, hex, leftHex, rightHex);
    }

    private static boolean isOnSea(Island island, Hex hex, Hex rightHex, Hex leftHex) {
        return island.getField(hex) == Field.SEA
                && island.getField(rightHex) == Field.SEA
                && island.getField(leftHex) == Field.SEA;
    }

    private static boolean isAdjacentToCoast(Island island, Hex hex, Hex leftHex, Hex rightHex) {
        List<Hex> neighborhoods = new ArrayList<>();
        Iterables.addAll(neighborhoods, hex.getNeighborhood());
        Iterables.addAll(neighborhoods, rightHex.getNeighborhood());
        Iterables.addAll(neighborhoods, leftHex.getNeighborhood());

        for (Hex neighbor : neighborhoods) {
            if (neighbor.equals(hex) || neighbor.equals(leftHex) || neighbor.equals(rightHex)) {
                continue;
            }

            if (island.getField(neighbor) != Field.SEA) {
                return true;
            }
        }

        return false;
    }
}
