package net.minecraft.server;

public class BiomeHell extends BiomeBase {

    public BiomeHell(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.t.clear();
        this.u.clear();
        this.v.clear();
        this.w.clear();
        this.t.add(new BiomeBase.BiomeMeta(EntityGhast.class, 50, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityPigZombie.class, 100, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityMagmaCube.class, 2, 4, 4));
        this.t.add(new BiomeBase.BiomeMeta(EntityEnderman.class, 1, 4, 4));
        this.s = new BiomeDecoratorHell();
    }
}
