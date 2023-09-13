package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class ArraySetSorted<T> extends AbstractSet<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private final Comparator<T> comparator;
    T[] contents;
    int size;

    private ArraySetSorted(int i, Comparator<T> comparator) {
        this.comparator = comparator;
        if (i < 0) {
            throw new IllegalArgumentException("Initial capacity (" + i + ") is negative");
        } else {
            this.contents = castRawArray(new Object[i]);
        }
    }

    public static <T extends Comparable<T>> ArraySetSorted<T> create() {
        return create(10);
    }

    public static <T extends Comparable<T>> ArraySetSorted<T> create(int i) {
        return new ArraySetSorted<>(i, Comparator.naturalOrder());
    }

    public static <T> ArraySetSorted<T> create(Comparator<T> comparator) {
        return create(comparator, 10);
    }

    public static <T> ArraySetSorted<T> create(Comparator<T> comparator, int i) {
        return new ArraySetSorted<>(i, comparator);
    }

    private static <T> T[] castRawArray(Object[] aobject) {
        return aobject;
    }

    private int findIndex(T t0) {
        return Arrays.binarySearch(this.contents, 0, this.size, t0, this.comparator);
    }

    private static int getInsertionPosition(int i) {
        return -i - 1;
    }

    public boolean add(T t0) {
        int i = this.findIndex(t0);

        if (i >= 0) {
            return false;
        } else {
            int j = getInsertionPosition(i);

            this.addInternal(t0, j);
            return true;
        }
    }

    private void grow(int i) {
        if (i > this.contents.length) {
            if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
                i = (int) Math.max(Math.min((long) this.contents.length + (long) (this.contents.length >> 1), 2147483639L), (long) i);
            } else if (i < 10) {
                i = 10;
            }

            Object[] aobject = new Object[i];

            System.arraycopy(this.contents, 0, aobject, 0, this.size);
            this.contents = castRawArray(aobject);
        }
    }

    private void addInternal(T t0, int i) {
        this.grow(this.size + 1);
        if (i != this.size) {
            System.arraycopy(this.contents, i, this.contents, i + 1, this.size - i);
        }

        this.contents[i] = t0;
        ++this.size;
    }

    void removeInternal(int i) {
        --this.size;
        if (i != this.size) {
            System.arraycopy(this.contents, i + 1, this.contents, i, this.size - i);
        }

        this.contents[this.size] = null;
    }

    private T getInternal(int i) {
        return this.contents[i];
    }

    public T addOrGet(T t0) {
        int i = this.findIndex(t0);

        if (i >= 0) {
            return this.getInternal(i);
        } else {
            this.addInternal(t0, getInsertionPosition(i));
            return t0;
        }
    }

    public boolean remove(Object object) {
        int i = this.findIndex(object);

        if (i >= 0) {
            this.removeInternal(i);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public T get(T t0) {
        int i = this.findIndex(t0);

        return i >= 0 ? this.getInternal(i) : null;
    }

    public T first() {
        return this.getInternal(0);
    }

    public T last() {
        return this.getInternal(this.size - 1);
    }

    public boolean contains(Object object) {
        int i = this.findIndex(object);

        return i >= 0;
    }

    public Iterator<T> iterator() {
        return new ArraySetSorted.a();
    }

    public int size() {
        return this.size;
    }

    public Object[] toArray() {
        return Arrays.copyOf(this.contents, this.size, Object[].class);
    }

    public <U> U[] toArray(U[] au) {
        if (au.length < this.size) {
            return Arrays.copyOf(this.contents, this.size, au.getClass());
        } else {
            System.arraycopy(this.contents, 0, au, 0, this.size);
            if (au.length > this.size) {
                au[this.size] = null;
            }

            return au;
        }
    }

    public void clear() {
        Arrays.fill(this.contents, 0, this.size, (Object) null);
        this.size = 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof ArraySetSorted) {
                ArraySetSorted<?> arraysetsorted = (ArraySetSorted) object;

                if (this.comparator.equals(arraysetsorted.comparator)) {
                    return this.size == arraysetsorted.size && Arrays.equals(this.contents, arraysetsorted.contents);
                }
            }

            return super.equals(object);
        }
    }

    private class a implements Iterator<T> {

        private int index;
        private int last = -1;

        a() {}

        public boolean hasNext() {
            return this.index < ArraySetSorted.this.size;
        }

        public T next() {
            if (this.index >= ArraySetSorted.this.size) {
                throw new NoSuchElementException();
            } else {
                this.last = this.index++;
                return ArraySetSorted.this.contents[this.last];
            }
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            } else {
                ArraySetSorted.this.removeInternal(this.last);
                --this.index;
                this.last = -1;
            }
        }
    }
}
