package net.minecraft.server;

import java.util.Random;

public class BiomeBigHills extends BiomeBase {

    private final WorldGenerator x;
    private final WorldGenTaiga2 y;
    private final BiomeBigHills.Type z;

    protected BiomeBigHills(BiomeBigHills.Type biomebighills_type, BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.x = new WorldGenMinable(Blocks.MONSTER_EGG.getBlockData().set(BlockMonsterEggs.VARIANT, BlockMonsterEggs.EnumMonsterEggVarient.STONE), 9);
        this.y = new WorldGenTaiga2(false);
        if (biomebighills_type == BiomeBigHills.Type.EXTRA_TREES) {
            this.s.z = 3;
        }

        this.u.add(new BiomeBase.BiomeMeta(EntityLlama.class, 5, 4, 6));
        this.z = biomebighills_type;
    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (random.nextInt(3) > 0 ? this.y : super.a(random));
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        super.a(world, random, blockposition);
        int i = 3 + random.nextInt(6);

        int j;
        int k;
        int l;

        for (j = 0; j < i; ++j) {
            k = random.nextInt(16);
            l = random.nextInt(28) + 4;
            int i1 = random.nextInt(16);
            BlockPosition blockposition1 = blockposition.a(k, l, i1);

            if (world.getType(blockposition1).getBlock() == Blocks.STONE) {
                world.setTypeAndData(blockposition1, Blocks.EMERALD_ORE.getBlockData(), 2);
            }
        }

        for (i = 0; i < 7; ++i) {
            j = random.nextInt(16);
            k = random.nextInt(64);
            l = random.nextInt(16);
            this.x.generate(world, random, blockposition.a(j, k, l));
        }

    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        this.q = Blocks.GRASS.getBlockData();
        this.r = Blocks.DIRT.getBlockData();
        if ((d0 < -1.0D || d0 > 2.0D) && this.z == BiomeBigHills.Type.MUTATED) {
            this.q = Blocks.GRAVEL.getBlockData();
            this.r = Blocks.GRAVEL.getBlockData();
        } else if (d0 > 1.0D && this.z != BiomeBigHills.Type.EXTRA_TREES) {
            this.q = Blocks.STONE.getBlockData();
            this.r = Blocks.STONE.getBlockData();
        }

        this.b(world, random, chunksnapshot, i, j, d0);
    }

    public static enum Type {

        NORMAL, EXTRA_TREES, MUTATED;

        private Type() {}
    }
}
