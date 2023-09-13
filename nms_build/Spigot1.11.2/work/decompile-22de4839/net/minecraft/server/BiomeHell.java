package net.minecraft.server;

public class BiomeHell extends BiomeBase {

    public BiomeHell(BiomeBase.a biomebase_a) {
        super(biomebase_a);
        this.u.clear();
        this.v.clear();
        this.w.clear();
        this.x.clear();
        this.u.add(new BiomeBase.BiomeMeta(EntityGhast.class, 50, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntityPigZombie.class, 100, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntityMagmaCube.class, 2, 4, 4));
        this.u.add(new BiomeBase.BiomeMeta(EntityEnderman.class, 1, 4, 4));
        this.t = new BiomeDecoratorHell();
    }
}
