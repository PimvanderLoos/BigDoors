package net.minecraft.server;

public final class BiomeMushrooms extends BiomeBase {

    public BiomeMushrooms() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.L).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.MUSHROOM).a(0.2F).b(0.3F).c(0.9F).d(1.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null));
        BiomeDecoratorGroups.b(this);
        this.a(BiomeDecoratorGroups.y);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.S(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.MOOSHROOM, 8, 4, 8));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
    }
}
