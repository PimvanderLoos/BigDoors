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
            this.contents = a(new Object[i]);
        }
    }

    public static <T extends Comparable<T>> ArraySetSorted<T> a() {
        return a(10);
    }

    public static <T extends Comparable<T>> ArraySetSorted<T> a(int i) {
        return new ArraySetSorted<>(i, Comparator.naturalOrder());
    }

    public static <T> ArraySetSorted<T> a(Comparator<T> comparator) {
        return a(comparator, 10);
    }

    public static <T> ArraySetSorted<T> a(Comparator<T> comparator, int i) {
        return new ArraySetSorted<>(i, comparator);
    }

    private static <T> T[] a(Object[] aobject) {
        return aobject;
    }

    private int c(T t0) {
        return Arrays.binarySearch(this.contents, 0, this.size, t0, this.comparator);
    }

    private static int b(int i) {
        return -i - 1;
    }

    public boolean add(T t0) {
        int i = this.c(t0);

        if (i >= 0) {
            return false;
        } else {
            int j = b(i);

            this.a(t0, j);
            return true;
        }
    }

    private void c(int i) {
        if (i > this.contents.length) {
            if (this.contents != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
                i = (int) Math.max(Math.min((long) this.contents.length + (long) (this.contents.length >> 1), 2147483639L), (long) i);
            } else if (i < 10) {
                i = 10;
            }

            Object[] aobject = new Object[i];

            System.arraycopy(this.contents, 0, aobject, 0, this.size);
            this.contents = a(aobject);
        }
    }

    private void a(T t0, int i) {
        this.c(this.size + 1);
        if (i != this.size) {
            System.arraycopy(this.contents, i, this.contents, i + 1, this.size - i);
        }

        this.contents[i] = t0;
        ++this.size;
    }

    void d(int i) {
        --this.size;
        if (i != this.size) {
            System.arraycopy(this.contents, i + 1, this.contents, i, this.size - i);
        }

        this.contents[this.size] = null;
    }

    private T e(int i) {
        return this.contents[i];
    }

    public T a(T t0) {
        int i = this.c(t0);

        if (i >= 0) {
            return this.e(i);
        } else {
            this.a(t0, b(i));
            return t0;
        }
    }

    public boolean remove(Object object) {
        int i = this.c(object);

        if (i >= 0) {
            this.d(i);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public T b(T t0) {
        int i = this.c(t0);

        return i >= 0 ? this.e(i) : null;
    }

    public T b() {
        return this.e(0);
    }

    public T c() {
        return this.e(this.size - 1);
    }

    public boolean contains(Object object) {
        int i = this.c(object);

        return i >= 0;
    }

    public Iterator<T> iterator() {
        return new ArraySetSorted.a();
    }

    public int size() {
        return this.size;
    }

    public Object[] toArray() {
        return (Object[]) this.contents.clone();
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
                ArraySetSorted.this.d(this.last);
                --this.index;
                this.last = -1;
            }
        }
    }
}
