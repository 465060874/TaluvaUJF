package map;

import com.google.common.collect.Iterators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class HexMapImpl<E> implements HexMap<E> {

    private final Map<Hex, E> map;

    HexMapImpl() {
        this.map = new HashMap<>();
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

    public void put(Hex hex, E element) {
        map.put(checkNotNull(hex), checkNotNull(element));
    }

    @Override
    public Iterator<Hex> iterator() {
        return Iterators.unmodifiableIterator(map.keySet().iterator());
    }

    @Override
    public int size() {
        return map.size();
    }
}
