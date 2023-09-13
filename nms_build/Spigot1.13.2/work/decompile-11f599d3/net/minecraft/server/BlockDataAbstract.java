package net.minecraft.server;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class BlockDataAbstract<O, S> implements IBlockDataHolder<S> {

    public static final Function<Entry<IBlockState<?>, Comparable<?>>, String> STATE_TO_VALUE = new Function<Entry<IBlockState<?>, Comparable<?>>, String>() {
        public String apply(@Nullable Entry<IBlockState<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();

                return iblockstate.a() + "=" + this.a(iblockstate, (Comparable) entry.getValue());
            }
        }

        private <T extends Comparable<T>> String a(IBlockState<T> iblockstate, Comparable<?> comparable) {
            return iblockstate.a(comparable);
        }
    };
    protected final O e_;
    private final ImmutableMap<IBlockState<?>, Comparable<?>> c;
    private final int d;
    private Table<IBlockState<?>, Comparable<?>, S> e;

    protected BlockDataAbstract(O o0, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
        this.e_ = o0;
        this.c = immutablemap;
        this.d = immutablemap.hashCode();
    }

    public <T extends Comparable<T>> S a(IBlockState<T> iblockstate) {
        return this.set(iblockstate, (Comparable) a(iblockstate.d(), this.get(iblockstate)));
    }

    protected static <T> T a(Collection<T> collection, T t0) {
        Iterator iterator = collection.iterator();

        do {
            if (!iterator.hasNext()) {
                return iterator.next();
            }
        } while (!iterator.next().equals(t0));

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return collection.iterator().next();
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.e_);
        if (!this.getStateMap().isEmpty()) {
            stringbuilder.append('[');
            stringbuilder.append((String) this.getStateMap().entrySet().stream().map(BlockDataAbstract.STATE_TO_VALUE).collect(Collectors.joining(",")));
            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    public Collection<IBlockState<?>> a() {
        return Collections.unmodifiableCollection(this.c.keySet());
    }

    public <T extends Comparable<T>> boolean b(IBlockState<T> iblockstate) {
        return this.c.containsKey(iblockstate);
    }

    public <T extends Comparable<T>> T get(IBlockState<T> iblockstate) {
        Comparable<?> comparable = (Comparable) this.c.get(iblockstate);

        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + iblockstate + " as it does not exist in " + this.e_);
        } else {
            return (Comparable) iblockstate.b().cast(comparable);
        }
    }

    public <T extends Comparable<T>, V extends T> S set(IBlockState<T> iblockstate, V v0) {
        Comparable<?> comparable = (Comparable) this.c.get(iblockstate);

        if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + iblockstate + " as it does not exist in " + this.e_);
        } else if (comparable == v0) {
            return this;
        } else {
            S s0 = this.e.get(iblockstate, v0);

            if (s0 == null) {
                throw new IllegalArgumentException("Cannot set property " + iblockstate + " to " + v0 + " on " + this.e_ + ", it is not an allowed value");
            } else {
                return s0;
            }
        }
    }

    public void a(Map<Map<IBlockState<?>, Comparable<?>>, S> map) {
        if (this.e != null) {
            throw new IllegalStateException();
        } else {
            Table<IBlockState<?>, Comparable<?>, S> table = HashBasedTable.create();
            UnmodifiableIterator unmodifiableiterator = this.c.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();
                Iterator iterator = iblockstate.d().iterator();

                while (iterator.hasNext()) {
                    Comparable<?> comparable = (Comparable) iterator.next();

                    if (comparable != entry.getValue()) {
                        table.put(iblockstate, comparable, map.get(this.b(iblockstate, comparable)));
                    }
                }
            }

            this.e = (Table) (table.isEmpty() ? table : ArrayTable.create(table));
        }
    }

    private Map<IBlockState<?>, Comparable<?>> b(IBlockState<?> iblockstate, Comparable<?> comparable) {
        Map<IBlockState<?>, Comparable<?>> map = Maps.newHashMap(this.c);

        map.put(iblockstate, comparable);
        return map;
    }

    public ImmutableMap<IBlockState<?>, Comparable<?>> getStateMap() {
        return this.c;
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return this.d;
    }
}
