package net.minecraft.server;

public class BiomeTheEnd extends BiomeBase {

    public BiomeTheEnd(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
        this.v.clear();
        this.w.clear();
        this.x.clear();
        this.u.add(new BiomeBase.BiomeMeta(EntityEnderman.class, 10, 4, 4));
        this.r = Blocks.DIRT.getBlockData();
        this.s = Blocks.DIRT.getBlockData();
        this.t = new BiomeTheEndDecorator();
    }
}
