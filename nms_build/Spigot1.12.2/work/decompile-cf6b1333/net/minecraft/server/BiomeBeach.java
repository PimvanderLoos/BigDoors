package net.minecraft.server;

public class BiomeBeach extends BiomeBase {

    public BiomeBeach(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
        this.q = Blocks.SAND.getBlockData();
        this.r = Blocks.SAND.getBlockData();
        this.s.z = -999;
        this.s.D = 0;
        this.s.F = 0;
        this.s.G = 0;
    }
}
