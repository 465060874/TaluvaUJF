package engine.rules;

import com.google.common.collect.ImmutableSet;
import map.Field;
import map.Hex;
import map.Island;
import map.Orientation;

import static engine.rules.Problem.*;

public class SeaTileRules {

    public static Problem validate(Island island, Hex volcanoHex, Orientation orientation) {
        if (island.isEmpty()) {
            return NONE;
        }

        ImmutableSet<Hex> hexes = ImmutableSet.of(
                volcanoHex,
                volcanoHex.getLeftNeighbor(orientation),
                volcanoHex.getRightNeighbor(orientation));
        return validate(island, hexes);
    }

    public static Problem validate(Island island, ImmutableSet<Hex> hexes) {
        return checkOnSea(island, hexes)
                .and(() -> checkAdjacentToCoast(island, hexes));
    }

    private static Problem checkOnSea(Island island, ImmutableSet<Hex> hexes) {
        for (Hex hex : hexes) {
            if (island.getField(hex) != Field.SEA) {
                return NOT_ALL_ON_SEA;
            }
        }

        return NONE;
    }

    private static Problem checkAdjacentToCoast(Island island, ImmutableSet<Hex> hexes) {
        for (Hex hex : hexes) {
            for (Hex neighbor : hex.getNeighborhood()) {
                if (!hexes.contains(neighbor) && island.getField(neighbor) != Field.SEA) {
                    return NONE;
                }
            }
        }

        return NOT_ADJACENT_TO_COAST;
    }
}
