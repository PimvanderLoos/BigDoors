package net.minecraft.server;

import java.util.Random;

public class BiomeSavanna extends BiomeBase {

    private static final WorldGenAcaciaTree x = new WorldGenAcaciaTree(false);

    protected BiomeSavanna(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.add(new BiomeBase.BiomeMeta(EntityHorse.class, 1, 2, 6));
        this.u.add(new BiomeBase.BiomeMeta(EntityHorseDonkey.class, 1, 1, 1));
        if (this.j() > 1.1F) {
            this.u.add(new BiomeBase.BiomeMeta(EntityLlama.class, 8, 4, 4));
        }

        this.s.z = 1;
        this.s.B = 4;
        this.s.C = 20;
    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (random.nextInt(5) > 0 ? BiomeSavanna.x : BiomeSavanna.m);
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        BiomeSavanna.l.a(BlockTallPlant.EnumTallFlowerVariants.GRASS);

        for (int i = 0; i < 7; ++i) {
            int j = random.nextInt(16) + 8;
            int k = random.nextInt(16) + 8;
            int l = random.nextInt(world.getHighestBlockYAt(blockposition.a(j, 0, k)).getY() + 32);

            BiomeSavanna.l.generate(world, random, blockposition.a(j, l, k));
        }

        super.a(world, random, blockposition);
    }

    public Class<? extends BiomeBase> g() {
        return BiomeSavanna.class;
    }
}
