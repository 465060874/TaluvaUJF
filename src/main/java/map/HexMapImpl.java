package map;

import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

class HexMapImpl<E> implements HexMap<E> {

    static final int INITIAL_CAPACITY = (1 << 9);

    private final Map<Hex, E> map;

    HexMapImpl() {
        this.map = new HashMap<>(INITIAL_CAPACITY);
    }

    private HexMapImpl(HexMapImpl<E> hexMap) {
        this.map = new HashMap<>();
        map.putAll(hexMap.map);
    }

    public boolean contains(Hex hex) {
        return map.containsKey(checkNotNull(hex));
    }

    public E get(Hex hex) {
        E element = map.get(checkNotNull(hex));
        if (element == null) {
            throw new IllegalStateException("Accessing unknown element at " + hex);
        }

        return element;
    }

    public E getOrDefault(Hex hex, E fallbackElement) {
        E element = map.get(checkNotNull(hex));
        if (element == null) {
            return fallbackElement;
        }

        return element;
    }

    public E put(Hex hex, E element) {
        return map.put(checkNotNull(hex), checkNotNull(element));
    }

    public E remove(Hex hex) {
        return map.remove(hex);
    }

    @Override
    public Set<Hex> hexes() {
        return Collections.unmodifiableSet(map.keySet());
    }

    @Override
    public Iterable<E> values() {
        return Iterables.unmodifiableIterable(map.values());
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public HexMap<E> copy() {
        return new HexMapImpl<>(this);
    }

    private static java.lang.reflect.Field TABLE_FIELD = null;

    @Override
    public double getHashFactor() {
        try {
            if (TABLE_FIELD == null) {
                TABLE_FIELD = HashMap.class.getDeclaredField("table");
                TABLE_FIELD.setAccessible(true);
            }

            Object[] table = (Object[]) TABLE_FIELD.get(map);
            int occupiedBuckets = 0;
            for (Object o : table) {
                if (o != null) {
                    occupiedBuckets++;
                }
            }
            return ((double) occupiedBuckets) / ((double) map.size());
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
