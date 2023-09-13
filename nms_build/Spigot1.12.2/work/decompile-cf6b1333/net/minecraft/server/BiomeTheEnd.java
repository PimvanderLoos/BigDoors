package net.minecraft.server;

public class BiomeTheEnd extends BiomeBase {

    public BiomeTheEnd(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.t.clear();
        this.u.clear();
        this.v.clear();
        this.w.clear();
        this.t.add(new BiomeBase.BiomeMeta(EntityEnderman.class, 10, 4, 4));
        this.q = Blocks.DIRT.getBlockData();
        this.r = Blocks.DIRT.getBlockData();
        this.s = new BiomeTheEndDecorator();
    }
}
