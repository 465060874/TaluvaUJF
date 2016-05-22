package engine.rules;

import com.google.common.base.Joiner;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;

public class Problems<P extends Enum<P>> implements Iterable<P> {

    private static final Joiner JOINER = Joiner.on(", ");

    public static <P extends Enum<P>> Problems<P> of(P first, P... others) {
        Problems<P> p = new Problems<>(first.getDeclaringClass());
        p.add(first);
        for (P problem : others) {
            p.add(problem);
        }

        return p;
    }

    public static <P extends Enum<P>> Problems<P> create(Class<P> clazz) {
        return new Problems<>(clazz);
    }

    private final P[] values;
    private int mask;

    private Problems(Class<P> clazz) {
        this.values = clazz.getEnumConstants();
        checkArgument(values.length <= 32);
        this.mask = 0;
    }

    public boolean isValid() {
        return mask == 0;
    }

    public boolean has(P problem) {
        return (mask & (1 << problem.ordinal())) != 0;
    }

    void add(P problem) {
        mask |= 1 << problem.ordinal();
    }

    @Override
    public Iterator<P> iterator() {
        if (values == null) {
            return ImmutableList.<P>of().iterator();
        }

        return new AbstractIterator<P>() {
            private int mask = Problems.this.mask;
            private int i = -1;

            @Override
            protected P computeNext() {
                do {
                    i++;
                    if (i >= values.length) {
                        return endOfData();
                    }
                } while ((mask & (1 << i)) == 0);
                return values[i];
            }
        };
    }

    @Override
    public int hashCode() {
        return mask;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Problems)) {
            return false;
        }

        Problems other = (Problems) obj;
        return values[0].getClass() == other.values[0].getClass()
                && mask == other.mask;
    }

    @Override
    public String toString() {
        return "[" + JOINER.join(this) + "]";
    }
}
