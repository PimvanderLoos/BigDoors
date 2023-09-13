package net.minecraft.server;

public final class BiomeIcePlainsSpikes extends BiomeBase {

    public BiomeIcePlainsSpikes() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, new WorldGenSurfaceConfigurationBase(Blocks.SNOW_BLOCK.getBlockData(), Blocks.DIRT.getBlockData(), Blocks.GRAVEL.getBlockData())).a(BiomeBase.Precipitation.SNOW).a(BiomeBase.Geography.ICY).a(0.425F).b(0.45000002F).c(0.0F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a("snowy_tundra"));
        BiomeDecoratorGroups.b(this);
        this.a(BiomeDecoratorGroups.y);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.f(this);
        BiomeDecoratorGroups.h(this);
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.ICE_SPIKE.b((WorldGenFeatureConfiguration) WorldGenFeatureConfiguration.k).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(3)))));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, WorldGenerator.ICE_PATCH.b((WorldGenFeatureConfiguration) (new WorldGenFeatureRadiusConfiguration(2))).a(WorldGenDecorator.b.a((WorldGenFeatureDecoratorConfiguration) (new WorldGenDecoratorFrequencyConfiguration(2)))));
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.H(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Y(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.ac(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.RABBIT, 10, 2, 3));
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.POLAR_BEAR, 1, 1, 2));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 95, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 20, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.STRAY, 80, 4, 4));
    }

    @Override
    public float f() {
        return 0.07F;
    }
}
