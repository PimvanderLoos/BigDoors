package net.minecraft.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class BlockStateList<O, S extends IBlockDataHolder<S>> {

    private static final Pattern a = Pattern.compile("^[a-z0-9_]+$");
    private final O b;
    private final ImmutableSortedMap<String, IBlockState<?>> c;
    private final ImmutableList<S> d;

    protected <A extends BlockDataAbstract<O, S>> BlockStateList(O o0, BlockStateList.b<O, S, A> blockstatelist_b, Map<String, IBlockState<?>> map) {
        this.b = o0;
        this.c = ImmutableSortedMap.copyOf(map);
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        ArrayList arraylist = Lists.newArrayList();
        Stream stream = Stream.of(Collections.emptyList());

        IBlockState iblockstate;

        for (UnmodifiableIterator unmodifiableiterator = this.c.values().iterator(); unmodifiableiterator.hasNext();stream = stream.flatMap((list) -> {
            return iblockstate.d().stream().map((comparable) -> {
                ArrayList arraylist = Lists.newArrayList(list);

                arraylist.add(comparable);
                return arraylist;
            });
        })) {
            iblockstate = (IBlockState) unmodifiableiterator.next();
        }

        stream.forEach((list) -> {
            Map map = MapGeneratorUtils.b(this.c.values(), list);
            BlockDataAbstract blockdataabstract = blockstatelist_b.create(object, ImmutableMap.copyOf(map));

            map1.put(map, blockdataabstract);
            list1.add(blockdataabstract);
        });
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            BlockDataAbstract blockdataabstract = (BlockDataAbstract) iterator.next();

            blockdataabstract.a((Map) linkedhashmap);
        }

        this.d = ImmutableList.copyOf(arraylist);
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
        return MoreObjects.toStringHelper(this).add("block", this.b).add("properties", this.c.values().stream().map(IBlockState::a).collect(Collectors.toList())).toString();
    }

    @Nullable
    public IBlockState<?> a(String s) {
        return (IBlockState) this.c.get(s);
    }

    public static class a<O, S extends IBlockDataHolder<S>> {

        private final O a;
        private final Map<String, IBlockState<?>> b = Maps.newHashMap();

        public a(O o0) {
            this.a = o0;
        }

        public BlockStateList.a<O, S> a(IBlockState<?>... aiblockstate) {
            IBlockState[] aiblockstate1 = aiblockstate;
            int i = aiblockstate.length;

            for (int j = 0; j < i; ++j) {
                IBlockState iblockstate = aiblockstate1[j];

                this.a(iblockstate);
                this.b.put(iblockstate.a(), iblockstate);
            }

            return this;
        }

        private <T extends Comparable<T>> void a(IBlockState<T> iblockstate) {
            String s = iblockstate.a();

            if (!BlockStateList.a.matcher(s).matches()) {
                throw new IllegalArgumentException(this.a + " has invalidly named property: " + s);
            } else {
                Collection collection = iblockstate.d();

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

                        Comparable comparable = (Comparable) iterator.next();

                        s1 = iblockstate.a(comparable);
                    } while (BlockStateList.a.matcher(s1).matches());

                    throw new IllegalArgumentException(this.a + " has property: " + s + " with invalidly named value: " + s1);
                }
            }
        }

        public <A extends BlockDataAbstract<O, S>> BlockStateList<O, S> a(BlockStateList.b<O, S, A> blockstatelist_b) {
            return new BlockStateList(this.a, blockstatelist_b, this.b);
        }
    }

    public interface b<O, S extends IBlockDataHolder<S>, A extends BlockDataAbstract<O, S>> {

        A create(O o0, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap);
    }
}
