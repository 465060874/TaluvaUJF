package util;

import java.util.List;
import java.util.Random;

public class Randoms {

    public static <E> E pickRandom(Random random, Iterable<List<? extends E>> lists) {
        int totalSize = 0;
        for (List<? extends E> list : lists) {
            totalSize += list.size();
        }

        int index = random.nextInt(totalSize);
        for (List<? extends E> list : lists) {
            if (index < list.size()) {
                return list.get(index);
            }

            index -= list.size();
        }

        throw new IllegalStateException("Should not happen");
    }
}
