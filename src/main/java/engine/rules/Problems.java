package engine.rules;

import com.google.common.base.Joiner;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;

public class Problems implements Iterable<Problem> {

    private static final Joiner JOINER = Joiner.on(", ");

    public static Problems of(Problem... problems) {
        Problems p = new Problems();
        for (Problem problem : problems) {
            p.add(problem);
        }

        return p;
    }

    private int mask;

    private Problems() {
        this.mask = 0;
    }

    public boolean isValid() {
        return mask == 0;
    }

    public boolean has(Problem problem) {
        return (mask & (1 << problem.ordinal())) != 0;
    }

    void add(Problem problem) {
        mask |= 1 << problem.ordinal();
    }

    @Override
    public Iterator<Problem> iterator() {
        if (mask == 0) {
            return ImmutableList.<Problem>of().iterator();
        }

        return new AbstractIterator<Problem>() {
            private int mask = Problems.this.mask;
            private int i = -1;

            @Override
            protected Problem computeNext() {
                do {
                    i++;
                    if (i >= Problem.values().length) {
                        return endOfData();
                    }
                } while ((mask & (1 << i)) == 0);
                return Problem.values()[i];
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
        return mask == other.mask;
    }

    @Override
    public String toString() {
        return "[" + JOINER.join(this) + "]";
    }
}
