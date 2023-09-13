package net.minecraft.server;

public final class BiomeColdBeach extends BiomeBase {

    public BiomeColdBeach() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.H).a(BiomeBase.Precipitation.SNOW).a(BiomeBase.Geography.BEACH).a(0.0F).b(0.025F).c(0.05F).d(0.3F).a((new BiomeFog.a()).b(4020182).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null));
        this.a(BiomeDecoratorGroups.b);
        this.a(BiomeDecoratorGroups.r);
        this.a(BiomeDecoratorGroups.i);
        this.a(BiomeDecoratorGroups.y);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Y(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
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
