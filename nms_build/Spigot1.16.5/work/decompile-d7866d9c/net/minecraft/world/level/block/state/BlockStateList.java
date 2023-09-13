package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class BlockStateList<O, S extends IBlockDataHolder<O, S>> {

    private static final Pattern a = Pattern.compile("^[a-z0-9_]+$");
    private final O b;
    private final ImmutableSortedMap<String, IBlockState<?>> c;
    private final ImmutableList<S> d;

    protected BlockStateList(Function<O, S> function, O o0, BlockStateList.b<O, S> blockstatelist_b, Map<String, IBlockState<?>> map) {
        this.b = o0;
        this.c = ImmutableSortedMap.copyOf(map);
        Supplier<S> supplier = () -> {
            return (IBlockDataHolder) function.apply(o0);
        };
        MapCodec<S> mapcodec = MapCodec.of(Encoder.empty(), Decoder.unit(supplier));

        Entry entry;

        for (UnmodifiableIterator unmodifiableiterator = this.c.entrySet().iterator(); unmodifiableiterator.hasNext(); mapcodec = a(mapcodec, supplier, (String) entry.getKey(), (IBlockState) entry.getValue())) {
            entry = (Entry) unmodifiableiterator.next();
        }

        Map<Map<IBlockState<?>, Comparable<?>>, S> map1 = Maps.newLinkedHashMap();
        List<S> list = Lists.newArrayList();
        Stream<List<Pair<IBlockState<?>, Comparable<?>>>> stream = Stream.of(Collections.emptyList());

        IBlockState iblockstate;

        for (UnmodifiableIterator unmodifiableiterator1 = this.c.values().iterator(); unmodifiableiterator1.hasNext();stream = stream.flatMap((list1) -> {
            return iblockstate.getValues().stream().map((comparable) -> {
                List<Pair<IBlockState<?>, Comparable<?>>> list2 = Lists.newArrayList(list1);

                list2.add(Pair.of(iblockstate, comparable));
                return list2;
            });
        })) {
            iblockstate = (IBlockState) unmodifiableiterator1.next();
        }

        stream.forEach((list1) -> {
            ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap = (ImmutableMap) list1.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
            S s0 = (IBlockDataHolder) blockstatelist_b.create(o0, immutablemap, mapcodec);

            map1.put(immutablemap, s0);
            list.add(s0);
        });
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            S s0 = (IBlockDataHolder) iterator.next();

            s0.a((Map) map1);
        }

        this.d = ImmutableList.copyOf(list);
    }

    private static <S extends IBlockDataHolder<?, S>, T extends Comparable<T>> MapCodec<S> a(MapCodec<S> mapcodec, Supplier<S> supplier, String s, IBlockState<T> iblockstate) {
        return Codec.mapPair(mapcodec, iblockstate.e().fieldOf(s).setPartial(() -> {
            return iblockstate.a((IBlockDataHolder) supplier.get());
        })).xmap((pair) -> {
            return (IBlockDataHolder) ((IBlockDataHolder) pair.getFirst()).set(iblockstate, ((IBlockState.a) pair.getSecond()).b());
        }, (iblockdataholder) -> {
            return Pair.of(iblockdataholder, iblockstate.a(iblockdataholder));
        });
    }

    public ImmutableList<S> a() {
        return this.d;
    }

    public S getBlockData() {
        return (IBlockDataHolder) this.d.get(0);
    }

    public O getBlock() {
        return this.b;
    }

    public Collection<IBlockState<?>> d() {
        return this.c.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.b).add("properties", this.c.values().stream().map(IBlockState::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public IBlockState<?> a(String s) {
        return (IBlockState) this.c.get(s);
    }

    public static class a<O, S extends IBlockDataHolder<O, S>> {

        private final O a;
        private final Map<String, IBlockState<?>> b = Maps.newHashMap();

        public a(O o0) {
            this.a = o0;
        }

        public BlockStateList.a<O, S> a(IBlockState<?>... aiblockstate) {
            IBlockState[] aiblockstate1 = aiblockstate;
            int i = aiblockstate.length;

            for (int j = 0; j < i; ++j) {
                IBlockState<?> iblockstate = aiblockstate1[j];

                this.a(iblockstate);
                this.b.put(iblockstate.getName(), iblockstate);
            }

            return this;
        }

        private <T extends Comparable<T>> void a(IBlockState<T> iblockstate) {
            String s = iblockstate.getName();

            if (!BlockStateList.a.matcher(s).matches()) {
                throw new IllegalArgumentException(this.a + " has invalidly named property: " + s);
            } else {
                Collection<T> collection = iblockstate.getValues();

                if (collection.size() <= 1) {
                    throw new IllegalArgumentException(this.a + " attempted use property " + s + " with <= 1 possible values");
                } else {
                    Iterator iterator = collection.iterator();

                    String s1;

                    do {
                        if (!iterator.hasNext()) {
                            if (this.b.containsKey(s)) {
                                throw new IllegalArgumentException(this.a + " has duplicate property: " + s);
                            }

                            return;
                        }

                        T t0 = (Comparable) iterator.next();

                        s1 = iblockstate.a(t0);
                    } while (BlockStateList.a.matcher(s1).matches());

                    throw new IllegalArgumentException(this.a + " has property: " + s + " with invalidly named value: " + s1);
                }
            }
        }

        public BlockStateList<O, S> a(Function<O, S> function, BlockStateList.b<O, S> blockstatelist_b) {
            return new BlockStateList<>(function, this.a, blockstatelist_b, this.b);
        }
    }

    public interface b<O, S> {

        S create(O o0, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap, MapCodec<S> mapcodec);
    }
}
