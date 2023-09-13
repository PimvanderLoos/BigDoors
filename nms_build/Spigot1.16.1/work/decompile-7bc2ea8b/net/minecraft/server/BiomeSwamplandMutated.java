package net.minecraft.server;

public final class BiomeSwamplandMutated extends BiomeBase {

    protected BiomeSwamplandMutated() {
        super((new BiomeBase.a()).a(WorldGenSurface.X, WorldGenSurface.D).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.SWAMP).a(-0.1F).b(0.3F).c(0.8F).d(0.9F).a((new BiomeFog.a()).b(6388580).c(2302743).a(12638463).a(CaveSoundSettings.b).a()).a("swamp"));
        this.a(BiomeDecoratorGroups.b);
        this.a(BiomeDecoratorGroups.B);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.o(this);
        BiomeDecoratorGroups.R(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ag(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ai(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SHEEP, 12, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PIG, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.CHICKEN, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.COW, 8, 4, 4));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 1, 1, 1));
    }
}
