package net.minecraft.server;

public final class BiomeMesaBryce extends BiomeBase {

    public BiomeMesaBryce() {
        super((new BiomeBase.a()).a(WorldGenSurface.aa, WorldGenSurface.K).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.MESA).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a("badlands"));
        BiomeDecoratorGroups.a(this);
        this.a(BiomeDecoratorGroups.C);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.k(this);
        BiomeDecoratorGroups.n(this);
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
