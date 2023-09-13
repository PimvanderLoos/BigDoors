package net.minecraft.server;

import com.google.common.collect.Lists;

public final class BiomeIcePlainsSpikes extends BiomeBase {

    public BiomeIcePlainsSpikes() {
        super((new BiomeBase.a()).a(new WorldGenSurfaceComposite<>(BiomeIcePlainsSpikes.au, new WorldGenSurfaceConfigurationBase(Blocks.SNOW_BLOCK.getBlockData(), Blocks.DIRT.getBlockData(), Blocks.GRAVEL.getBlockData()))).a(BiomeBase.Precipitation.SNOW).a(BiomeBase.Geography.ICY).a(0.425F).b(0.45000002F).c(0.0F).d(0.5F).a(4159204).b(329011).a("snowy_tundra"));
        this.a(WorldGenerator.f, (WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL)));
        this.a(WorldGenerator.m, (WorldGenFeatureConfiguration) (new WorldGenFeatureStrongholdConfiguration()));
        this.a(WorldGenStage.Features.AIR, a((WorldGenCarver) BiomeIcePlainsSpikes.b, (WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.14285715F))));
        this.a(WorldGenStage.Features.AIR, a((WorldGenCarver) BiomeIcePlainsSpikes.d, (WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        this.a();
        this.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, a(WorldGenerator.am, new WorldGenFeatureLakeConfiguration(Blocks.WATER), BiomeIcePlainsSpikes.K, new WorldGenDecoratorLakeChanceConfiguration(4)));
        this.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, a(WorldGenerator.am, new WorldGenFeatureLakeConfiguration(Blocks.LAVA), BiomeIcePlainsSpikes.J, new WorldGenDecoratorLakeChanceConfiguration(80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, a(WorldGenerator.ad, WorldGenFeatureConfiguration.e, BiomeIcePlainsSpikes.L, new WorldGenDecoratorDungeonConfiguration(8)));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, a(WorldGenerator.V, WorldGenFeatureConfiguration.e, BiomeIcePlainsSpikes.g, new WorldGenDecoratorFrequencyConfiguration(3)));
        this.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, a(WorldGenerator.al, new WorldGenFeatureRadiusConfiguration(2), BiomeIcePlainsSpikes.g, new WorldGenDecoratorFrequencyConfiguration(2)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.DIRT.getBlockData(), 33), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 256)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.GRAVEL.getBlockData(), 33), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 256)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.GRANITE.getBlockData(), 33), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.DIORITE.getBlockData(), 33), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.ANDESITE.getBlockData(), 33), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.COAL_ORE.getBlockData(), 17), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 128)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.IRON_ORE.getBlockData(), 9), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 64)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.GOLD_ORE.getBlockData(), 9), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(2, 0, 0, 32)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.REDSTONE_ORE.getBlockData(), 8), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 16)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.DIAMOND_ORE.getBlockData(), 8), BiomeIcePlainsSpikes.t, new WorldGenFeatureChanceDecoratorCountConfiguration(1, 0, 0, 16)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.LAPIS_ORE.getBlockData(), 7), BiomeIcePlainsSpikes.A, new WorldGenDecoratorHeightAverageConfiguration(1, 16, 16)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.ai, new WorldGenFeatureCircleConfiguration(Blocks.SAND, 7, 2, Lists.newArrayList(new Block[] { Blocks.DIRT, Blocks.GRASS_BLOCK})), BiomeIcePlainsSpikes.h, new WorldGenDecoratorFrequencyConfiguration(3)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.ai, new WorldGenFeatureCircleConfiguration(Blocks.CLAY, 4, 1, Lists.newArrayList(new Block[] { Blocks.DIRT, Blocks.CLAY})), BiomeIcePlainsSpikes.h, new WorldGenDecoratorFrequencyConfiguration(1)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.ai, new WorldGenFeatureCircleConfiguration(Blocks.GRAVEL, 6, 2, Lists.newArrayList(new Block[] { Blocks.DIRT, Blocks.GRASS_BLOCK})), BiomeIcePlainsSpikes.h, new WorldGenDecoratorFrequencyConfiguration(1)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.A, WorldGenFeatureConfiguration.e, BiomeIcePlainsSpikes.s, new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, (WorldGenFeatureComposite) a(WorldGenerator.G, BiomeIcePlainsSpikes.i, new WorldGenDecoratorFrequencyConfiguration(2)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.M, new WorldGenFeatureTallGrassConfiguration(Blocks.GRASS.getBlockData()), BiomeIcePlainsSpikes.j, new WorldGenDecoratorFrequencyConfiguration(1)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.ah, new WorldGenFeatureMushroomConfiguration(Blocks.BROWN_MUSHROOM), BiomeIcePlainsSpikes.p, new WorldGenDecoratorChanceConfiguration(4)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.ah, new WorldGenFeatureMushroomConfiguration(Blocks.RED_MUSHROOM), BiomeIcePlainsSpikes.p, new WorldGenDecoratorChanceConfiguration(8)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.Z, WorldGenFeatureConfiguration.e, BiomeIcePlainsSpikes.j, new WorldGenDecoratorFrequencyConfiguration(10)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.Y, WorldGenFeatureConfiguration.e, BiomeIcePlainsSpikes.p, new WorldGenDecoratorChanceConfiguration(32)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.at, new WorldGenFeatureFlowingConfiguration(FluidTypes.WATER), BiomeIcePlainsSpikes.u, new WorldGenFeatureChanceDecoratorCountConfiguration(50, 8, 8, 256)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.at, new WorldGenFeatureFlowingConfiguration(FluidTypes.LAVA), BiomeIcePlainsSpikes.v, new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)));
        this.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, a(WorldGenerator.aa, WorldGenFeatureConfiguration.e, BiomeIcePlainsSpikes.n, WorldGenFeatureDecoratorConfiguration.e));
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

    public float e() {
        return 0.07F;
    }
}
