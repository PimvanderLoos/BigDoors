package net.minecraft.server;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class BlockStateList {

    private static final Pattern a = Pattern.compile("^[a-z0-9_]+$");
    private static final Function<IBlockState<?>, String> b = new Function() {
        @Nullable
        public String a(@Nullable IBlockState<?> iblockstate) {
            return iblockstate == null ? "<NULL>" : iblockstate.a();
        }

        @Nullable
        public Object apply(@Nullable Object object) {
            return this.a((IBlockState) object);
        }
    };
    private final Block c;
    private final ImmutableSortedMap<String, IBlockState<?>> d;
    private final ImmutableList<IBlockData> e;

    public BlockStateList(Block block, IBlockState<?>... aiblockstate) {
        this.c = block;
        HashMap hashmap = Maps.newHashMap();
        IBlockState[] aiblockstate1 = aiblockstate;
        int i = aiblockstate.length;

        for (int j = 0; j < i; ++j) {
            IBlockState iblockstate = aiblockstate1[j];

            a(block, iblockstate);
            hashmap.put(iblockstate.a(), iblockstate);
        }

        this.d = ImmutableSortedMap.copyOf(hashmap);
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        ArrayList arraylist = Lists.newArrayList();
        Iterable iterable = IteratorUtils.a(this.e());
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            List list = (List) iterator.next();
            Map map = MapGeneratorUtils.b(this.d.values(), list);
            BlockStateList.BlockData blockstatelist_blockdata = new BlockStateList.BlockData(block, ImmutableMap.copyOf(map), null);

            linkedhashmap.put(map, blockstatelist_blockdata);
            arraylist.add(blockstatelist_blockdata);
        }

        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            BlockStateList.BlockData blockstatelist_blockdata1 = (BlockStateList.BlockData) iterator.next();

            blockstatelist_blockdata1.a((Map) linkedhashmap);
        }

        this.e = ImmutableList.copyOf(arraylist);
    }

    public static <T extends Comparable<T>> String a(Block block, IBlockState<T> iblockstate) {
        String s = iblockstate.a();

        if (!BlockStateList.a.matcher(s).matches()) {
            throw new IllegalArgumentException("Block: " + block.getClass() + " has invalidly named property: " + s);
        } else {
            Iterator iterator = iblockstate.c().iterator();

            String s1;

            do {
                if (!iterator.hasNext()) {
                    return s;
                }

                Comparable comparable = (Comparable) iterator.next();

                s1 = iblockstate.a(comparable);
            } while (BlockStateList.a.matcher(s1).matches());

            throw new IllegalArgumentException("Block: " + block.getClass() + " has property: " + s + " with invalidly named value: " + s1);
        }
    }

    public ImmutableList<IBlockData> a() {
        return this.e;
    }

    private List<Iterable<Comparable<?>>> e() {
        ArrayList arraylist = Lists.newArrayList();
        ImmutableCollection immutablecollection = this.d.values();
        UnmodifiableIterator unmodifiableiterator = immutablecollection.iterator();

        while (unmodifiableiterator.hasNext()) {
            IBlockState iblockstate = (IBlockState) unmodifiableiterator.next();

            arraylist.add(iblockstate.c());
        }

        return arraylist;
    }

    public IBlockData getBlockData() {
        return (IBlockData) this.e.get(0);
    }

    public Block getBlock() {
        return this.c;
    }

    public Collection<IBlockState<?>> d() {
        return this.d.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", Block.REGISTRY.b(this.c)).add("properties", Iterables.transform(this.d.values(), BlockStateList.b)).toString();
    }

    @Nullable
    public IBlockState<?> a(String s) {
        return (IBlockState) this.d.get(s);
    }

    static class BlockData extends BlockDataAbstract {

        private final Block a;
        private final ImmutableMap<IBlockState<?>, Comparable<?>> b;
        private ImmutableTable<IBlockState<?>, Comparable<?>, IBlockData> c;

        private BlockData(Block block, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
            this.a = block;
            this.b = immutablemap;
        }

        public Collection<IBlockState<?>> s() {
            return Collections.unmodifiableCollection(this.b.keySet());
        }

        public <T extends Comparable<T>> T get(IBlockState<T> iblockstate) {
            Comparable comparable = (Comparable) this.b.get(iblockstate);

            if (comparable == null) {
                throw new IllegalArgumentException("Cannot get property " + iblockstate + " as it does not exist in " + this.a.s());
            } else {
                return (Comparable) iblockstate.b().cast(comparable);
            }
        }

        public <T extends Comparable<T>, V extends T> IBlockData set(IBlockState<T> iblockstate, V v0) {
            Comparable comparable = (Comparable) this.b.get(iblockstate);

            if (comparable == null) {
                throw new IllegalArgumentException("Cannot set property " + iblockstate + " as it does not exist in " + this.a.s());
            } else if (comparable == v0) {
                return this;
            } else {
                IBlockData iblockdata = (IBlockData) this.c.get(iblockstate, v0);

                if (iblockdata == null) {
                    throw new IllegalArgumentException("Cannot set property " + iblockstate + " to " + v0 + " on block " + Block.REGISTRY.b(this.a) + ", it is not an allowed value");
                } else {
                    return iblockdata;
                }
            }
        }

        public ImmutableMap<IBlockState<?>, Comparable<?>> t() {
            return this.b;
        }

        public Block getBlock() {
            return this.a;
        }

        public boolean equals(Object object) {
            return this == object;
        }

        public int hashCode() {
            return this.b.hashCode();
        }

        public void a(Map<Map<IBlockState<?>, Comparable<?>>, BlockStateList.BlockData> map) {
            if (this.c != null) {
                throw new IllegalStateException();
            } else {
                HashBasedTable hashbasedtable = HashBasedTable.create();
                UnmodifiableIterator unmodifiableiterator = this.b.entrySet().iterator();

                while (unmodifiableiterator.hasNext()) {
                    Entry entry = (Entry) unmodifiableiterator.next();
                    IBlockState iblockstate = (IBlockState) entry.getKey();
                    Iterator iterator = iblockstate.c().iterator();

                    while (iterator.hasNext()) {
                        Comparable comparable = (Comparable) iterator.next();

                        if (comparable != entry.getValue()) {
                            hashbasedtable.put(iblockstate, comparable, map.get(this.b(iblockstate, comparable)));
                        }
                    }
                }

                this.c = ImmutableTable.copyOf(hashbasedtable);
            }
        }

        private Map<IBlockState<?>, Comparable<?>> b(IBlockState<?> iblockstate, Comparable<?> comparable) {
            HashMap hashmap = Maps.newHashMap(this.b);

            hashmap.put(iblockstate, comparable);
            return hashmap;
        }

        public Material getMaterial() {
            return this.a.q(this);
        }

        public boolean b() {
            return this.a.l(this);
        }

        public boolean a(Entity entity) {
            return this.a.a((IBlockData) this, entity);
        }

        public int c() {
            return this.a.m(this);
        }

        public int d() {
            return this.a.o(this);
        }

        public boolean f() {
            return this.a.p(this);
        }

        public MaterialMapColor a(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.a.c(this, iblockaccess, blockposition);
        }

        public IBlockData a(EnumBlockRotation enumblockrotation) {
            return this.a.a((IBlockData) this, enumblockrotation);
        }

        public IBlockData a(EnumBlockMirror enumblockmirror) {
            return this.a.a((IBlockData) this, enumblockmirror);
        }

        public boolean g() {
            return this.a.c((IBlockData) this);
        }

        public EnumRenderType i() {
            return this.a.a((IBlockData) this);
        }

        public boolean k() {
            return this.a.r(this);
        }

        public boolean l() {
            return this.a.isOccluding(this);
        }

        public boolean m() {
            return this.a.isPowerSource(this);
        }

        public int a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.a.b((IBlockData) this, iblockaccess, blockposition, enumdirection);
        }

        public boolean n() {
            return this.a.isComplexRedstone(this);
        }

        public int a(World world, BlockPosition blockposition) {
            return this.a.c(this, world, blockposition);
        }

        public float b(World world, BlockPosition blockposition) {
            return this.a.a((IBlockData) this, world, blockposition);
        }

        public float a(EntityHuman entityhuman, World world, BlockPosition blockposition) {
            return this.a.getDamage(this, entityhuman, world, blockposition);
        }

        public int b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.a.c(this, iblockaccess, blockposition, enumdirection);
        }

        public EnumPistonReaction o() {
            return this.a.h(this);
        }

        public IBlockData c(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.a.updateState(this, iblockaccess, blockposition);
        }

        public boolean p() {
            return this.a.b((IBlockData) this);
        }

        @Nullable
        public AxisAlignedBB d(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.a.a((IBlockData) this, iblockaccess, blockposition);
        }

        public void a(World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
            this.a.a(this, world, blockposition, axisalignedbb, list, entity, flag);
        }

        public AxisAlignedBB e(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.a.b(this, iblockaccess, blockposition);
        }

        public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
            return this.a.a(this, world, blockposition, vec3d, vec3d1);
        }

        public boolean q() {
            return this.a.k(this);
        }

        public Vec3D f(IBlockAccess iblockaccess, BlockPosition blockposition) {
            return this.a.f(this, iblockaccess, blockposition);
        }

        public boolean a(World world, BlockPosition blockposition, int i, int j) {
            return this.a.a(this, world, blockposition, i, j);
        }

        public void doPhysics(World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
            this.a.a(this, world, blockposition, block, blockposition1);
        }

        public boolean r() {
            return this.a.t(this);
        }

        public EnumBlockFaceShape d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
            return this.a.a(iblockaccess, (IBlockData) this, blockposition, enumdirection);
        }

        BlockData(Block block, ImmutableMap immutablemap, Object object) {
            this(block, immutablemap);
        }
    }
}
