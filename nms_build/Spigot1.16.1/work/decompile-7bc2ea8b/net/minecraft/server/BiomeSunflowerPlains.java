package net.minecraft.server;

public final class BiomeSunflowerPlains extends BiomeBase {

    protected BiomeSunflowerPlains() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.D).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.PLAINS).a(0.125F).b(0.05F).c(0.8F).d(0.4F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a("plains"));
        BiomeDecoratorGroups.b(this);
        this.a(BiomeDecoratorGroups.y);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.aa(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.ay).a(WorldGenDecorator.d.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.T(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.aC).a(WorldGenDecorator.e.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(10)))));
        BiomeDecoratorGroups.ab(this);
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, WorldGenerator.RANDOM_PATCH.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.an).a(WorldGenDecorator.j.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorDungeonConfiguration(32)))));
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SHEEP, 12, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PIG, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.CHICKEN, 10, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.COW, 8, 4, 4));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.HORSE, 5, 2, 6));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.DONKEY, 1, 1, 3));
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
