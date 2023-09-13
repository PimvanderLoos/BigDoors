package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class NonNullList<E> extends AbstractList<E> {

    private final List<E> list;
    @Nullable
    private final E defaultValue;

    public static <E> NonNullList<E> create() {
        return new NonNullList<>(Lists.newArrayList(), (Object) null);
    }

    public static <E> NonNullList<E> createWithCapacity(int i) {
        return new NonNullList<>(Lists.newArrayListWithCapacity(i), (Object) null);
    }

    public static <E> NonNullList<E> withSize(int i, E e0) {
        Validate.notNull(e0);
        Object[] aobject = new Object[i];

        Arrays.fill(aobject, e0);
        return new NonNullList<>(Arrays.asList(aobject), e0);
    }

    @SafeVarargs
    public static <E> NonNullList<E> of(E e0, E... ae) {
        return new NonNullList<>(Arrays.asList(ae), e0);
    }

    protected NonNullList(List<E> list, @Nullable E e0) {
        this.list = list;
        this.defaultValue = e0;
    }

    @Nonnull
    public E get(int i) {
        return this.list.get(i);
    }

    public E set(int i, E e0) {
        Validate.notNull(e0);
        return this.list.set(i, e0);
    }

    public void add(int i, E e0) {
        Validate.notNull(e0);
        this.list.add(i, e0);
    }

    public E remove(int i) {
        return this.list.remove(i);
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        if (this.defaultValue == null) {
            super.clear();
        } else {
            for (int i = 0; i < this.size(); ++i) {
                this.set(i, this.defaultValue);
            }
        }

    }
}
