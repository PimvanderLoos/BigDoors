package net.minecraft.server;

public class BiomeStoneBeach extends BiomeBase {

    public BiomeStoneBeach(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
        this.q = Blocks.STONE.getBlockData();
        this.r = Blocks.STONE.getBlockData();
        this.s.z = -999;
        this.s.D = 0;
        this.s.F = 0;
        this.s.G = 0;
    }
}
