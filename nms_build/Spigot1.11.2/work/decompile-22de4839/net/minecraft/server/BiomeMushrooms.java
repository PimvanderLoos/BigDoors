package net.minecraft.server;

public class BiomeMushrooms extends BiomeBase {

    public BiomeMushrooms(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.t.z = -100;
        this.t.B = -100;
        this.t.C = -100;
        this.t.E = 1;
        this.t.K = 1;
        this.r = Blocks.MYCELIUM.getBlockData();
        this.u.clear();
        this.v.clear();
        this.w.clear();
        this.v.add(new BiomeBase.BiomeMeta(EntityMushroomCow.class, 8, 4, 8));
    }
}
