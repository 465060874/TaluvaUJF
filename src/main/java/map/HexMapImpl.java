package map;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

class HexMapImpl<E> implements HexMap<E> {

    static final int CAPACITY = (1 << 6);
    private static final int HASH_MASK = CAPACITY - 1;

    private static final class Entry<E> {
        private final Hex hex;
        private E value;
        private Entry<E> next;

        public Entry(Hex hex, E value, Entry<E> next) {
            this.hex = hex;
            this.value = value;
            this.next = next;
        }

        public Entry(Entry<E> other) {
            this.hex = other.hex;
            this.value = other.value;
            this.next = other.next == null
                    ? null
                    : new Entry<>(other.next);
        }
    }

    private final Set<Hex> hexes;
    private final Entry<E>[] table;
    private int size;

    HexMapImpl() {
        this.hexes = new LinkedHashSet<>();
        @SuppressWarnings("unchecked")
        Entry<E>[] safeTable = new Entry[CAPACITY];
        this.table = safeTable;
        this.size = 0;
    }

    private HexMapImpl(HexMapImpl<E> other) {
        this.hexes = new HashSet<>(other.hexes);

        @SuppressWarnings("unchecked")
        Entry<E>[] safeTable = new Entry[CAPACITY];
        for (int i = 0; i < other.table.length; i++) {
            Entry<E> otherEntry = other.table[i];
            if (otherEntry != null) {
                safeTable[i] = new Entry<>(otherEntry);
            }
        }
        this.table = safeTable;

        this.size = other.size;
    }

    private int hashIndex(Hex hex) {
        return hex.hashCode() & HASH_MASK;
    }

    @Override
    public boolean contains(Hex hex) {
        return table[hashIndex(hex)] != null;
    }

    private Entry<E> doGet(Hex hex) {
        int line = hex.getLine();
        int diag = hex.getDiag();

        Entry<E> entry = table[hashIndex(hex)];
        while (entry != null) {
            if (hex == entry.hex
                    || (entry.hex.getLine() == line && entry.hex.getDiag() == diag)) {
                return entry;
            }

            entry = entry.next;
        }

        return null;
    }

    @Override
    public E get(Hex hex) {
        Entry<E> entry = doGet(hex);
        if (entry == null) {
            throw new IllegalStateException("Accessing unknown element at " + hex);
        }

        return entry.value;
    }

    @Override
    public E getOrDefault(Hex hex, E fallbackValue) {
        Entry<E> entry = doGet(hex);
        if (entry == null) {
            return fallbackValue;
        }

        return entry.value;
    }

    @Override
    public E put(Hex hex, E element) {
        hexes.add(hex);

        Entry<E> entry = doGet(hex);
        if (entry != null) {
            E before = entry.value;
            entry.value = element;
            return before;
        }

        int index = hashIndex(hex);
        table[index] = new Entry<>(hex, element, table[index]);
        size++;
        return null;
    }

    @Override
    public E remove(Hex hex) {
        Entry<E> entry = doGet(hex);
        if (entry == null) {
            return null;
        }

        int index = hashIndex(hex);
        Entry<E> previous = table[index];
        if (previous == entry) {
            // Suppression en tête
            table[index] = entry.next;
        }
        else {
            // Suppression du chainage
            while (previous.next != entry) {
                previous = previous.next;
            }

            previous.next = entry.next;
        }

        size--;

        hexes.remove(hex);
        Entry<E> hexEntry = table[index];
        while (hexEntry != null) {
            hexes.add(hexEntry.hex);
            hexEntry = hexEntry.next;
        }

        return entry.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Set<Hex> hexes() {
        return hexes;
    }


    @Override
    public HexMap<E> copy() {
        return new HexMapImpl<>(this);
    }

    @Override
    public double getHashFactor() {
        double occupiedBuckets = 0;
        for (Entry<E> entry : table) {
            if (entry != null) {
                occupiedBuckets++;
            }
        }

        return occupiedBuckets / (double) size;
    }
}
