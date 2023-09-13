package net.minecraft.server;

public final class BiomeMesaPlateauMutated extends BiomeBase {

    public BiomeMesaPlateauMutated() {
        super((new BiomeBase.a()).a(WorldGenSurface.Z, WorldGenSurface.K).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.MESA).a(0.45F).b(0.3F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a("wooded_badlands_plateau"));
        BiomeDecoratorGroups.a(this);
        this.a(BiomeDecoratorGroups.C);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.k(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.G(this);
        BiomeDecoratorGroups.O(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ad(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
    }
}
