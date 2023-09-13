package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class WorldGenNether extends StructureGenerator {

    private final List<BiomeBase.BiomeMeta> a = Lists.newArrayList();

    public WorldGenNether() {
        this.a.add(new BiomeBase.BiomeMeta(EntityBlaze.class, 10, 2, 3));
        this.a.add(new BiomeBase.BiomeMeta(EntityPigZombie.class, 5, 4, 4));
        this.a.add(new BiomeBase.BiomeMeta(EntitySkeletonWither.class, 8, 5, 5));
        this.a.add(new BiomeBase.BiomeMeta(EntitySkeleton.class, 2, 5, 5));
        this.a.add(new BiomeBase.BiomeMeta(EntityMagmaCube.class, 3, 4, 4));
    }

    public String a() {
        return "Fortress";
    }

    public List<BiomeBase.BiomeMeta> b() {
        return this.a;
    }

    protected boolean a(int i, int j) {
        int k = i >> 4;
        int l = j >> 4;

        this.f.setSeed((long) (k ^ l << 4) ^ this.g.getSeed());
        this.f.nextInt();
        return this.f.nextInt(3) != 0 ? false : (i != (k << 4) + 4 + this.f.nextInt(8) ? false : j == (l << 4) + 4 + this.f.nextInt(8));
    }

    protected StructureStart b(int i, int j) {
        return new WorldGenNether.WorldGenNetherStart(this.g, this.f, i, j);
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition, boolean flag) {
        boolean flag1 = true;
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;

        for (int k = 0; k <= 1000; ++k) {
            for (int l = -k; l <= k; ++l) {
                boolean flag2 = l == -k || l == k;

                for (int i1 = -k; i1 <= k; ++i1) {
                    boolean flag3 = i1 == -k || i1 == k;

                    if (flag2 || flag3) {
                        int j1 = i + l;
                        int k1 = j + i1;

                        if (this.a(j1, k1) && (!flag || !world.b(j1, k1))) {
                            return new BlockPosition((j1 << 4) + 8, 64, (k1 << 4) + 8);
                        }
                    }
                }
            }
        }

        return null;
    }

    public static class WorldGenNetherStart extends StructureStart {

        public WorldGenNetherStart() {}

        public WorldGenNetherStart(World world, Random random, int i, int j) {
            super(i, j);
            WorldGenNetherPieces.WorldGenNetherPiece15 worldgennetherpieces_worldgennetherpiece15 = new WorldGenNetherPieces.WorldGenNetherPiece15(random, (i << 4) + 2, (j << 4) + 2);

            this.a.add(worldgennetherpieces_worldgennetherpiece15);
            worldgennetherpieces_worldgennetherpiece15.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.a, random);
            List list = worldgennetherpieces_worldgennetherpiece15.d;

            while (!list.isEmpty()) {
                int k = random.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(k);

                structurepiece.a((StructurePiece) worldgennetherpieces_worldgennetherpiece15, this.a, random);
            }

            this.d();
            this.a(world, random, 48, 70);
        }
    }
}
