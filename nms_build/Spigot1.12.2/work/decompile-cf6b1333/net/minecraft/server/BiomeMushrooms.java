package net.minecraft.server;

public class BiomeMushrooms extends BiomeBase {

    public BiomeMushrooms(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.s.z = -100;
        this.s.B = -100;
        this.s.C = -100;
        this.s.E = 1;
        this.s.K = 1;
        this.q = Blocks.MYCELIUM.getBlockData();
        this.t.clear();
        this.u.clear();
        this.v.clear();
        this.u.add(new BiomeBase.BiomeMeta(EntityMushroomCow.class, 8, 4, 8));
    }
}
