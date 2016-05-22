package engine.rules;

import com.google.common.collect.Iterables;
import data.VolcanoTile;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

import java.util.ArrayList;
import java.util.List;

import static engine.rules.PlacementProblem.*;

public class SeaPlacementRules {

    public static Problems<PlacementProblem>
            validate(Island island, VolcanoTile tile, Hex hex, Orientation orientation) {
        Hex rightHex = hex.getRightNeighbor(orientation);
        Hex leftHex = hex.getLeftNeighbor(orientation);

        Problems<PlacementProblem> problems = Problems.create(PlacementProblem.class);
        checkOnSea(island, hex, rightHex, leftHex, problems);
        checkAdjacentToCoast(island, hex, leftHex, rightHex, problems);
        return problems;
    }

    private static void checkOnSea(Island island, Hex hex, Hex rightHex, Hex leftHex,
            Problems<PlacementProblem> problems) {
        if (island.getField(hex) != Field.SEA
                || island.getField(rightHex) != Field.SEA
                || island.getField(leftHex) != Field.SEA) {
            problems.add(NOT_ALL_ON_SEA);
        }
    }

    private static void checkAdjacentToCoast(Island island, Hex hex, Hex leftHex, Hex rightHex,
            Problems<PlacementProblem> problems) {
        List<Hex> neighborhoods = new ArrayList<>();
        Iterables.addAll(neighborhoods, hex.getNeighborhood());
        Iterables.addAll(neighborhoods, rightHex.getNeighborhood());
        Iterables.addAll(neighborhoods, leftHex.getNeighborhood());

        for (Hex neighbor : neighborhoods) {
            if (!neighbor.equals(hex)
                    && !neighbor.equals(leftHex)
                    && !neighbor.equals(rightHex)
                    && island.getField(neighbor) != Field.SEA) {
                return;
            }
        }

        problems.add(NOT_ADJACENT_TO_COAST);
    }
}
