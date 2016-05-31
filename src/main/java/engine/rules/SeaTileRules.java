package engine.rules;

import com.google.common.collect.ImmutableSet;
import data.VolcanoTile;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

import static engine.rules.Problem.NOT_ADJACENT_TO_COAST;
import static engine.rules.Problem.NOT_ALL_ON_SEA;

public class SeaTileRules {

    public static Problems validate(Island island, VolcanoTile tile, Hex volcanoHex, Orientation orientation) {
        if (island.isEmpty()) {
            return Problems.of();
        }

        ImmutableSet<Hex> hexes = ImmutableSet.of(
                volcanoHex,
                volcanoHex.getLeftNeighbor(orientation),
                volcanoHex.getRightNeighbor(orientation));
        return validate(island, tile, hexes);
    }

    public static Problems validate(Island island, VolcanoTile tile, ImmutableSet<Hex> hexes) {
        Problems problems = Problems.of();
        checkOnSea(island, hexes, problems);
        checkAdjacentToCoast(island, hexes, problems);
        return problems;
    }

    private static void checkOnSea(Island island, ImmutableSet<Hex> hexes, Problems problems) {
        if (hexes.stream().map(island::getField).anyMatch(f -> f != Field.SEA)) {
            problems.add(NOT_ALL_ON_SEA);
        }
    }

    private static void checkAdjacentToCoast(Island island, ImmutableSet<Hex> hexes, Problems problems) {
        for (Hex hex : hexes) {
            for (Hex neighbor : hex.getNeighborhood()) {
                if (!hexes.contains(neighbor) && island.getField(neighbor) != Field.SEA) {
                    return;
                }
            }
        }

        problems.add(NOT_ADJACENT_TO_COAST);
    }
}
