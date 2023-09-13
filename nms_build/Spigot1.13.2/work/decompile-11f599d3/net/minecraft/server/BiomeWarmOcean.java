package net.minecraft.server;

import com.google.common.collect.Lists;

public class BiomeWarmOcean extends BiomeBase {

    public BiomeWarmOcean() {
        super((new BiomeBase.a()).a(new WorldGenSurfaceComposite<>(BiomeWarmOcean.au, BiomeWarmOcean.an)).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.OCEAN).a(-1.0F).b(0.1F).c(0.5F).d(0.5F).a(4445678).b(270131).a((String) null));
        this.a(WorldGenerator.o, (WorldGenFeatureConfiguration) (new WorldGenFeatureOceanRuinConfiguration(WorldGenFeatureOceanRuin.Temperature.WARM, 0.3F, 0.9F)));
        this.a(WorldGenerator.f, (WorldGenFeatureConfiguration) (new WorldGenMineshaftConfiguration(0.004D, WorldGenMineshaft.Type.NORMAL)));
        this.a(WorldGenerator.k, (WorldGenFeatureConfiguration) (new WorldGenFeatureShipwreckConfiguration(false)));
        this.a(WorldGenStage.Features.AIR, a((WorldGenCarver) BiomeWarmOcean.b, (WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
        this.a(WorldGenStage.Features.AIR, a((WorldGenCarver) BiomeWarmOcean.d, (WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        this.a(WorldGenStage.Features.LIQUID, a((WorldGenCarver) BiomeWarmOcean.e, (WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.02F))));
        this.a(WorldGenStage.Features.LIQUID, a((WorldGenCarver) BiomeWarmOcean.f, (WorldGenFeatureConfiguration) (new WorldGenFeatureConfigurationChance(0.06666667F))));
        this.a();
        this.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, a(WorldGenerator.am, new WorldGenFeatureLakeConfiguration(Blocks.WATER), BiomeWarmOcean.K, new WorldGenDecoratorLakeChanceConfiguration(4)));
        this.a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, a(WorldGenerator.am, new WorldGenFeatureLakeConfiguration(Blocks.LAVA), BiomeWarmOcean.J, new WorldGenDecoratorLakeChanceConfiguration(80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES, a(WorldGenerator.ad, WorldGenFeatureConfiguration.e, BiomeWarmOcean.L, new WorldGenDecoratorDungeonConfiguration(8)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.DIRT.getBlockData(), 33), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 256)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.GRAVEL.getBlockData(), 33), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 256)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.GRANITE.getBlockData(), 33), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.DIORITE.getBlockData(), 33), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.ANDESITE.getBlockData(), 33), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(10, 0, 0, 80)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.COAL_ORE.getBlockData(), 17), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 128)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.IRON_ORE.getBlockData(), 9), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(20, 0, 0, 64)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.GOLD_ORE.getBlockData(), 9), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(2, 0, 0, 32)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.REDSTONE_ORE.getBlockData(), 8), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(8, 0, 0, 16)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.DIAMOND_ORE.getBlockData(), 8), BiomeWarmOcean.t, new WorldGenFeatureChanceDecoratorCountConfiguration(1, 0, 0, 16)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.an, new WorldGenFeatureOreConfiguration(WorldGenFeatureOreConfiguration.a, Blocks.LAPIS_ORE.getBlockData(), 7), BiomeWarmOcean.A, new WorldGenDecoratorHeightAverageConfiguration(1, 16, 16)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.ai, new WorldGenFeatureCircleConfiguration(Blocks.SAND, 7, 2, Lists.newArrayList(new Block[] { Blocks.DIRT, Blocks.GRASS_BLOCK})), BiomeWarmOcean.h, new WorldGenDecoratorFrequencyConfiguration(3)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.ai, new WorldGenFeatureCircleConfiguration(Blocks.CLAY, 4, 1, Lists.newArrayList(new Block[] { Blocks.DIRT, Blocks.CLAY})), BiomeWarmOcean.h, new WorldGenDecoratorFrequencyConfiguration(1)));
        this.a(WorldGenStage.Decoration.UNDERGROUND_ORES, a(WorldGenerator.ai, new WorldGenFeatureCircleConfiguration(Blocks.GRAVEL, 6, 2, Lists.newArrayList(new Block[] { Blocks.DIRT, Blocks.GRASS_BLOCK})), BiomeWarmOcean.h, new WorldGenDecoratorFrequencyConfiguration(1)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.ap, new WorldGenFeatureRandomChoiceConfiguration(new WorldGenerator[] { WorldGenerator.s}, new WorldGenFeatureConfiguration[] { WorldGenFeatureConfiguration.e}, new float[] { 0.1F}, WorldGenerator.C, WorldGenFeatureConfiguration.e), BiomeWarmOcean.s, new WorldGenDecoratorFrequencyExtraChanceConfiguration(0, 0.1F, 1)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, (WorldGenFeatureComposite) a(WorldGenerator.G, BiomeWarmOcean.i, new WorldGenDecoratorFrequencyConfiguration(2)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.M, new WorldGenFeatureTallGrassConfiguration(Blocks.GRASS.getBlockData()), BiomeWarmOcean.j, new WorldGenDecoratorFrequencyConfiguration(1)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.ah, new WorldGenFeatureMushroomConfiguration(Blocks.BROWN_MUSHROOM), BiomeWarmOcean.p, new WorldGenDecoratorChanceConfiguration(4)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.ah, new WorldGenFeatureMushroomConfiguration(Blocks.RED_MUSHROOM), BiomeWarmOcean.p, new WorldGenDecoratorChanceConfiguration(8)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.Z, WorldGenFeatureConfiguration.e, BiomeWarmOcean.j, new WorldGenDecoratorFrequencyConfiguration(10)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.Y, WorldGenFeatureConfiguration.e, BiomeWarmOcean.p, new WorldGenDecoratorChanceConfiguration(32)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.at, new WorldGenFeatureFlowingConfiguration(FluidTypes.WATER), BiomeWarmOcean.u, new WorldGenFeatureChanceDecoratorCountConfiguration(50, 8, 8, 256)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.at, new WorldGenFeatureFlowingConfiguration(FluidTypes.LAVA), BiomeWarmOcean.v, new WorldGenFeatureChanceDecoratorCountConfiguration(20, 8, 16, 256)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.aq, new WorldGenFeatureRandom2(new WorldGenerator[] { WorldGenerator.aA, WorldGenerator.aC, WorldGenerator.aB}, new WorldGenFeatureConfiguration[] { WorldGenFeatureConfiguration.e, WorldGenFeatureConfiguration.e, WorldGenFeatureConfiguration.e}), BiomeWarmOcean.D, new WorldGenDecoratorNoiseConfiguration(20, 400.0D)));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.ay, new WorldGenFeatureSeaGrassConfiguration(80, 0.3D), BiomeWarmOcean.B, WorldGenFeatureDecoratorConfiguration.e));
        this.a(WorldGenStage.Decoration.VEGETAL_DECORATION, a(WorldGenerator.aD, new WorldGenFeatureKelpConfiguration(20), BiomeWarmOcean.r, new WorldGenDecoratorChanceConfiguration(16)));
        this.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, a(WorldGenerator.aa, WorldGenFeatureConfiguration.e, BiomeWarmOcean.n, WorldGenFeatureDecoratorConfiguration.e));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.SQUID, 10, 4, 4));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.PUFFERFISH, 15, 1, 3));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.TROPICAL_FISH, 25, 8, 8));
        this.a(EnumCreatureType.WATER_CREATURE, new BiomeBase.BiomeMeta(EntityTypes.DOLPHIN, 2, 1, 2));
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
