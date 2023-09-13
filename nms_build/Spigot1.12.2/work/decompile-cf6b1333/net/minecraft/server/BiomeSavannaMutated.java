package net.minecraft.server;

import java.util.Random;

public class BiomeSavannaMutated extends BiomeSavanna {

    public BiomeSavannaMutated(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.s.z = 2;
        this.s.B = 2;
        this.s.C = 5;
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        this.q = Blocks.GRASS.getBlockData();
        this.r = Blocks.DIRT.getBlockData();
        if (d0 > 1.75D) {
            this.q = Blocks.STONE.getBlockData();
            this.r = Blocks.STONE.getBlockData();
        } else if (d0 > -0.5D) {
            this.q = Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.COARSE_DIRT);
        }

        this.b(world, random, chunksnapshot, i, j, d0);
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        this.s.a(world, random, this, blockposition);
    }
}
