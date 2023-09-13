package net.minecraft.server;

import com.google.common.collect.ImmutableList;

public class BiomeFrozenDeepOcean extends BiomeBase {

    protected static final NoiseGenerator3 t = new NoiseGenerator3(new SeededRandom(3456L), ImmutableList.of(-2, -1, 0));

    public BiomeFrozenDeepOcean() {
        super((new BiomeBase.a()).a(WorldGenSurface.ab, WorldGenSurface.D).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.OCEAN).a(-1.8F).b(0.1F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(3750089).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null));
        this.a(BiomeDecoratorGroups.m);
        this.a(BiomeDecoratorGroups.l);
        BiomeDecoratorGroups.c(this);
        this.a(BiomeDecoratorGroups.D);
        BiomeDecoratorGroups.e(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.ap(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.aq(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.w(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Y(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SQUID, 1, 1, 4));
        this.a(EnumCreatureType.WATER_AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.SALMON, 15, 1, 5));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.POLAR_BEAR, 1, 1, 2));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.DROWNED, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
    }

    @Override
    protected float a(BlockPosition blockposition) {
        float f = this.getTemperature();
        double d0 = BiomeFrozenDeepOcean.t.a((double) blockposition.getX() * 0.05D, (double) blockposition.getZ() * 0.05D, false) * 7.0D;
        double d1 = BiomeFrozenDeepOcean.f.a((double) blockposition.getX() * 0.2D, (double) blockposition.getZ() * 0.2D, false);
        double d2 = d0 + d1;

        if (d2 < 0.3D) {
            double d3 = BiomeFrozenDeepOcean.f.a((double) blockposition.getX() * 0.09D, (double) blockposition.getZ() * 0.09D, false);

            if (d3 < 0.8D) {
                f = 0.2F;
            }
        }

        if (blockposition.getY() > 64) {
            float f1 = (float) (BiomeFrozenDeepOcean.e.a((double) ((float) blockposition.getX() / 8.0F), (double) ((float) blockposition.getZ() / 8.0F), false) * 4.0D);

            return f - (f1 + (float) blockposition.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return f;
        }
    }
}
