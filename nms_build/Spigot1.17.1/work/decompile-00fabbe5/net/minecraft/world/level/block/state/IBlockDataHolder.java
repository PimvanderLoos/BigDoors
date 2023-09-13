package net.minecraft.world.level.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.IBlockState;

public abstract class IBlockDataHolder<O, S> {

    public static final String NAME_TAG = "Name";
    public static final String PROPERTIES_TAG = "Properties";
    public static final Function<Entry<IBlockState<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Entry<IBlockState<?>, Comparable<?>>, String>() {
        public String apply(@Nullable Entry<IBlockState<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();
                String s = iblockstate.getName();

                return s + "=" + this.a(iblockstate, (Comparable) entry.getValue());
            }
        }

        private <T extends Comparable<T>> String a(IBlockState<T> iblockstate, Comparable<?> comparable) {
            return iblockstate.a(comparable);
        }
    };
    protected final O owner;
    private final ImmutableMap<IBlockState<?>, Comparable<?>> values;
    private Table<IBlockState<?>, Comparable<?>, S> neighbours;
    protected final MapCodec<S> propertiesCodec;

    protected IBlockDataHolder(O o0, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<S> mapcodec) {
        this.owner = o0;
        this.values = immutablemap;
        this.propertiesCodec = mapcodec;
    }

    public <T extends Comparable<T>> S a(IBlockState<T> iblockstate) {
        return this.set(iblockstate, (Comparable) a(iblockstate.getValues(), (Object) this.get(iblockstate)));
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

        stringbuilder.append(this.owner);
        if (!this.getStateMap().isEmpty()) {
            stringbuilder.append('[');
            stringbuilder.append((String) this.getStateMap().entrySet().stream().map(IBlockDataHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    public Collection<IBlockState<?>> s() {
        return Collections.unmodifiableCollection(this.values.keySet());
    }

    public <T extends Comparable<T>> boolean b(IBlockState<T> iblockstate) {
        return this.values.containsKey(iblockstate);
    }

    public <T extends Comparable<T>> T get(IBlockState<T> iblockstate) {
        Comparable<?> comparable = (Comparable) this.values.get(iblockstate);

        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + iblockstate + " as it does not exist in " + this.owner);
        } else {
            return (Comparable) iblockstate.getType().cast(comparable);
        }
    }

    public <T extends Comparable<T>> Optional<T> d(IBlockState<T> iblockstate) {
        Comparable<?> comparable = (Comparable) this.values.get(iblockstate);

        return comparable == null ? Optional.empty() : Optional.of((Comparable) iblockstate.getType().cast(comparable));
    }

    public <T extends Comparable<T>, V extends T> S set(IBlockState<T> iblockstate, V v0) {
        Comparable<?> comparable = (Comparable) this.values.get(iblockstate);

        if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + iblockstate + " as it does not exist in " + this.owner);
        } else if (comparable == v0) {
            return this;
        } else {
            S s0 = this.neighbours.get(iblockstate, v0);

            if (s0 == null) {
                throw new IllegalArgumentException("Cannot set property " + iblockstate + " to " + v0 + " on " + this.owner + ", it is not an allowed value");
            } else {
                return s0;
            }
        }
    }

    public void a(Map<Map<IBlockState<?>, Comparable<?>>, S> map) {
        if (this.neighbours != null) {
            throw new IllegalStateException();
        } else {
            Table<IBlockState<?>, Comparable<?>, S> table = HashBasedTable.create();
            UnmodifiableIterator unmodifiableiterator = this.values.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();
                Iterator iterator = iblockstate.getValues().iterator();

                while (iterator.hasNext()) {
                    Comparable<?> comparable = (Comparable) iterator.next();

                    if (comparable != entry.getValue()) {
                        table.put(iblockstate, comparable, map.get(this.b(iblockstate, comparable)));
                    }
                }
            }

            this.neighbours = (Table) (table.isEmpty() ? table : ArrayTable.create(table));
        }
    }

    private Map<IBlockState<?>, Comparable<?>> b(IBlockState<?> iblockstate, Comparable<?> comparable) {
        Map<IBlockState<?>, Comparable<?>> map = Maps.newHashMap(this.values);

        map.put(iblockstate, comparable);
        return map;
    }

    public ImmutableMap<IBlockState<?>, Comparable<?>> getStateMap() {
        return this.values;
    }

    protected static <O, S extends IBlockDataHolder<O, S>> Codec<S> a(Codec<O> codec, Function<O, S> function) {
        return codec.dispatch("Name", (iblockdataholder) -> {
            return iblockdataholder.owner;
        }, (object) -> {
            S s0 = (IBlockDataHolder) function.apply(object);

            return s0.getStateMap().isEmpty() ? Codec.unit(s0) : s0.propertiesCodec.fieldOf("Properties").codec();
        });
    }
}
