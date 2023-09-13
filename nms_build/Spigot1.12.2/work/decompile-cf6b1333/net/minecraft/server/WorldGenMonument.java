package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class WorldGenMonument extends StructureGenerator {

    private int d;
    private int h;
    public static final List<BiomeBase> a = Arrays.asList(new BiomeBase[] { Biomes.a, Biomes.z, Biomes.i, Biomes.l, Biomes.m});
    public static final List<BiomeBase> b = Arrays.asList(new BiomeBase[] { Biomes.z});
    private static final List<BiomeBase.BiomeMeta> i = Lists.newArrayList();

    public WorldGenMonument() {
        this.d = 32;
        this.h = 5;
    }

    public WorldGenMonument(Map<String, String> map) {
        this();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("spacing")) {
                this.d = MathHelper.a((String) entry.getValue(), this.d, 1);
            } else if (((String) entry.getKey()).equals("separation")) {
                this.h = MathHelper.a((String) entry.getValue(), this.h, 1);
            }
        }

    }

    public String a() {
        return "Monument";
    }

    protected boolean a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            i -= this.d - 1;
        }

        if (j < 0) {
            j -= this.d - 1;
        }

        int i1 = i / this.d;
        int j1 = j / this.d;
        Random random = this.g.a(i1, j1, 10387313);

        i1 *= this.d;
        j1 *= this.d;
        i1 += (random.nextInt(this.d - this.h) + random.nextInt(this.d - this.h)) / 2;
        j1 += (random.nextInt(this.d - this.h) + random.nextInt(this.d - this.h)) / 2;
        if (k == i1 && l == j1) {
            if (!this.g.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 16, WorldGenMonument.b)) {
                return false;
            }

            boolean flag = this.g.getWorldChunkManager().a(k * 16 + 8, l * 16 + 8, 29, WorldGenMonument.a);

            if (flag) {
                return true;
            }
        }

        return false;
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        this.g = world;
        return a(world, this, blockposition, this.d, this.h, 10387313, true, 100, flag);
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenMonument.WorldGenMonumentStart(this.g, this.f, i, j);
    }

    public List<BiomeBase.BiomeMeta> b() {
        return WorldGenMonument.i;
    }

    static {
        WorldGenMonument.i.add(new BiomeBase.BiomeMeta(EntityGuardian.class, 1, 2, 4));
    }

    public static class WorldGenMonumentStart extends StructureStart {

        private final Set<ChunkCoordIntPair> c = Sets.newHashSet();
        private boolean d;

        public WorldGenMonumentStart() {}

        public WorldGenMonumentStart(World world, Random random, int i, int j) {
            super(i, j);
            this.b(world, random, i, j);
        }

        private void b(World world, Random random, int i, int j) {
            random.setSeed(world.getSeed());
            long k = random.nextLong();
            long l = random.nextLong();
            long i1 = (long) i * k;
            long j1 = (long) j * l;

            random.setSeed(i1 ^ j1 ^ world.getSeed());
            int k1 = i * 16 + 8 - 29;
            int l1 = j * 16 + 8 - 29;
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);

            this.a.add(new WorldGenMonumentPieces.WorldGenMonumentPiece1(random, k1, l1, enumdirection));
            this.d();
            this.d = true;
        }

        public void a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (!this.d) {
                this.a.clear();
                this.b(world, random, this.e(), this.f());
            }

            super.a(world, random, structureboundingbox);
        }

        public boolean a(ChunkCoordIntPair chunkcoordintpair) {
            return this.c.contains(chunkcoordintpair) ? false : super.a(chunkcoordintpair);
        }

        public void b(ChunkCoordIntPair chunkcoordintpair) {
            super.b(chunkcoordintpair);
            this.c.add(chunkcoordintpair);
        }

        public void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setInt("X", chunkcoordintpair.x);
                nbttagcompound1.setInt("Z", chunkcoordintpair.z);
                nbttaglist.add(nbttagcompound1);
            }

            nbttagcompound.set("Processed", nbttaglist);
        }

        public void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            if (nbttagcompound.hasKeyOfType("Processed", 9)) {
                NBTTagList nbttaglist = nbttagcompound.getList("Processed", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.get(i);

                    this.c.add(new ChunkCoordIntPair(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Z")));
                }
            }

        }
    }
}
