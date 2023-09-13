package net.minecraft.server;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class WorldGenStronghold extends StructureGenerator {

    private final List<BiomeBase> a;
    private boolean b;
    private ChunkCoordIntPair[] d;
    private double h;
    private int i;

    public WorldGenStronghold() {
        this.d = new ChunkCoordIntPair[128];
        this.h = 32.0D;
        this.i = 3;
        this.a = Lists.newArrayList();
        Iterator iterator = BiomeBase.REGISTRY_ID.iterator();

        while (iterator.hasNext()) {
            BiomeBase biomebase = (BiomeBase) iterator.next();

            if (biomebase != null && biomebase.j() > 0.0F) {
                this.a.add(biomebase);
            }
        }

    }

    public WorldGenStronghold(Map<String, String> map) {
        this();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("distance")) {
                this.h = MathHelper.a((String) entry.getValue(), this.h, 1.0D);
            } else if (((String) entry.getKey()).equals("count")) {
                this.d = new ChunkCoordIntPair[MathHelper.a((String) entry.getValue(), this.d.length, 1)];
            } else if (((String) entry.getKey()).equals("spread")) {
                this.i = MathHelper.a((String) entry.getValue(), this.i, 1);
            }
        }

    }

    public String a() {
        return "Stronghold";
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        if (!this.b) {
            this.c();
            this.b = true;
        }

        BlockPosition blockposition1 = null;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(0, 0, 0);
        double d0 = Double.MAX_VALUE;
        ChunkCoordIntPair[] achunkcoordintpair = this.d;
        int i = achunkcoordintpair.length;

        for (int j = 0; j < i; ++j) {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[j];

            blockposition_mutableblockposition.c((chunkcoordintpair.x << 4) + 8, 32, (chunkcoordintpair.z << 4) + 8);
            double d1 = blockposition_mutableblockposition.n(blockposition);

            if (blockposition1 == null) {
                blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                d0 = d1;
            } else if (d1 < d0) {
                blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                d0 = d1;
            }
        }

        return blockposition1;
    }

    protected boolean a(int i, int j) {
        if (!this.b) {
            this.c();
            this.b = true;
        }

        ChunkCoordIntPair[] achunkcoordintpair = this.d;
        int k = achunkcoordintpair.length;

        for (int l = 0; l < k; ++l) {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[l];

            if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
                return true;
            }
        }

        return false;
    }

    private void c() {
        this.a(this.g);
        int i = 0;
        ObjectIterator objectiterator = this.c.values().iterator();

        while (objectiterator.hasNext()) {
            StructureStart structurestart = (StructureStart) objectiterator.next();

            if (i < this.d.length) {
                this.d[i++] = new ChunkCoordIntPair(structurestart.e(), structurestart.f());
            }
        }

        Random random = new Random();

        random.setSeed(this.g.getSeed());
        double d0 = random.nextDouble() * 3.141592653589793D * 2.0D;
        int j = 0;
        int k = 0;
        int l = this.c.size();

        if (l < this.d.length) {
            for (int i1 = 0; i1 < this.d.length; ++i1) {
                double d1 = 4.0D * this.h + this.h * (double) j * 6.0D + (random.nextDouble() - 0.5D) * this.h * 2.5D;
                int j1 = (int) Math.round(Math.cos(d0) * d1);
                int k1 = (int) Math.round(Math.sin(d0) * d1);
                BlockPosition blockposition = this.g.getWorldChunkManager().a((j1 << 4) + 8, (k1 << 4) + 8, 112, this.a, random);

                if (blockposition != null) {
                    j1 = blockposition.getX() >> 4;
                    k1 = blockposition.getZ() >> 4;
                }

                if (i1 >= l) {
                    this.d[i1] = new ChunkCoordIntPair(j1, k1);
                }

                d0 += 6.283185307179586D / (double) this.i;
                ++k;
                if (k == this.i) {
                    ++j;
                    k = 0;
                    this.i += 2 * this.i / (j + 1);
                    this.i = Math.min(this.i, this.d.length - i1);
                    d0 += random.nextDouble() * 3.141592653589793D * 2.0D;
                }
            }
        }

    }

    protected StructureStart b(int i, int j) {
        WorldGenStronghold.WorldGenStronghold2Start worldgenstronghold_worldgenstronghold2start;

        for (worldgenstronghold_worldgenstronghold2start = new WorldGenStronghold.WorldGenStronghold2Start(this.g, this.f, i, j); worldgenstronghold_worldgenstronghold2start.c().isEmpty() || ((WorldGenStrongholdPieces.WorldGenStrongholdStart) worldgenstronghold_worldgenstronghold2start.c().get(0)).b == null; worldgenstronghold_worldgenstronghold2start = new WorldGenStronghold.WorldGenStronghold2Start(this.g, this.f, i, j)) {
            ;
        }

        return worldgenstronghold_worldgenstronghold2start;
    }

    public static class WorldGenStronghold2Start extends StructureStart {

        public WorldGenStronghold2Start() {}

        public WorldGenStronghold2Start(World world, Random random, int i, int j) {
            super(i, j);
            WorldGenStrongholdPieces.b();
            WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(0, random, (i << 4) + 2, (j << 4) + 2);

            this.a.add(worldgenstrongholdpieces_worldgenstrongholdstart);
            worldgenstrongholdpieces_worldgenstrongholdstart.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.a, random);
            List list = worldgenstrongholdpieces_worldgenstrongholdstart.c;

            while (!list.isEmpty()) {
                int k = random.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(k);

                structurepiece.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.a, random);
            }

            this.d();
            this.a(world, random, 10);
        }
    }
}
