package data;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static data.FieldType.*;

/**
 * Contient la liste des 48 tuiles standard du jeu
 */

public class StandardVolcanoTiles {

    public static final List<VolcanoTile> LIST;

    static {
        ImmutableList.Builder<VolcanoTile> builder = ImmutableList.builder();

        tiles(1, CLEARING, CLEARING, builder);
        tiles(1, JUNGLE, JUNGLE, builder);
        tiles(1, SAND, SAND, builder);
        tiles(1, ROCK, ROCK, builder);
        tiles(1, LAKE, LAKE, builder);

        tiles(1, CLEARING, LAKE, builder);
        tiles(1, LAKE, CLEARING, builder);

        tiles(1, SAND, LAKE, builder);
        tiles(1, LAKE, SAND, builder);

        tiles(1, ROCK, LAKE, builder);
        tiles(1, LAKE, ROCK, builder);

        tiles(1, ROCK, SAND, builder);
        tiles(2, SAND, ROCK, builder);

        tiles(1, LAKE, JUNGLE, builder);
        tiles(2, JUNGLE, LAKE, builder);

        tiles(2, CLEARING, ROCK, builder);
        tiles(2, ROCK, CLEARING, builder);

        tiles(2, CLEARING, SAND, builder);
        tiles(2, SAND, CLEARING, builder);

        tiles(2, ROCK, JUNGLE, builder);
        tiles(2, JUNGLE, ROCK, builder);

        tiles(4, SAND, JUNGLE, builder);
        tiles(4, JUNGLE, SAND, builder);

        tiles(5, CLEARING, JUNGLE, builder);
        tiles(6, JUNGLE, CLEARING, builder);

        LIST = builder.build();
    }

    private static void tiles(int n, FieldType left, FieldType right, ImmutableList.Builder<VolcanoTile> builder) {
        VolcanoTile tile = new VolcanoTile(left, right);
        for (int i = 0; i < n; i++) {
            builder.add(tile);
        }
    }
}
