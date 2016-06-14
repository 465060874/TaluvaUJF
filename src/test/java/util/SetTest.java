package util;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class SetTest {

    public static <E> Set<E> assertNoDuplicatesAndCreateSet(Iterable<E> iterable) {
        Set<E> set = new HashSet<>();
        for (E element : iterable) {
            // On vérifie l'unicité au fur et à mesure
            assertTrue("Duplicated elements" + element, set.add(element));
        }

        return set;
    }
}
