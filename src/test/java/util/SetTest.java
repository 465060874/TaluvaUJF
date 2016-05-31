package util;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;

public class SetTest {

    public static <E> Set<E> assertNoDuplicatesAndCreateSet(Iterable<E> iterable) {
        Set<E> set = new HashSet<>();
        for (E element : iterable) {
            // On vérifie l'unicité au fur et a mesure
            if (!set.add(element)) {
                fail("Duplicated elements" + element);
            }
        }

        return set;
    }
}
