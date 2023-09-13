package net.minecraft.server;

import java.util.Random;

public class BiomeSavannaMutated extends BiomeSavanna {

    public BiomeSavannaMutated(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.t.z = 2;
        this.t.B = 2;
        this.t.C = 5;
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        this.r = Blocks.GRASS.getBlockData();
        this.s = Blocks.DIRT.getBlockData();
        if (d0 > 1.75D) {
            this.r = Blocks.STONE.getBlockData();
            this.s = Blocks.STONE.getBlockData();
        } else if (d0 > -0.5D) {
            this.r = Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.COARSE_DIRT);
        }

        this.b(world, random, chunksnapshot, i, j, d0);
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        this.t.a(world, random, this, blockposition);
    }
}
