package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.block.BlockSweetBerryBush;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.DualNoiseProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.material.FluidTypes;

public class VegetationFeatures {

    public static final WorldGenFeatureConfigured<WorldGenFeatureConfigurationChance, ?> BAMBOO_NO_PODZOL = FeatureUtils.register("bamboo_no_podzol", WorldGenerator.BAMBOO.configured(new WorldGenFeatureConfigurationChance(0.0F)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureConfigurationChance, ?> BAMBOO_SOME_PODZOL = FeatureUtils.register("bamboo_some_podzol", WorldGenerator.BAMBOO.configured(new WorldGenFeatureConfigurationChance(0.2F)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> VINES = FeatureUtils.register("vines", WorldGenerator.VINES.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<?, ?> PATCH_BROWN_MUSHROOM = FeatureUtils.register("patch_brown_mushroom", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.BROWN_MUSHROOM))))));
    public static final WorldGenFeatureConfigured<?, ?> PATCH_RED_MUSHROOM = FeatureUtils.register("patch_red_mushroom", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.RED_MUSHROOM))))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_SUNFLOWER = FeatureUtils.register("patch_sunflower", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SUNFLOWER))))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_PUMPKIN = FeatureUtils.register("patch_pumpkin", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.PUMPKIN))), List.of(Blocks.GRASS_BLOCK))));
    public static final WorldGenFeatureConfigured<?, ?> PATCH_BERRY_BUSH = FeatureUtils.register("patch_berry_bush", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple((IBlockData) Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(BlockSweetBerryBush.AGE, 3)))), List.of(Blocks.GRASS_BLOCK))));
    public static final WorldGenFeatureConfigured<?, ?> PATCH_TAIGA_GRASS = FeatureUtils.register("patch_taiga_grass", WorldGenerator.RANDOM_PATCH.configured(grassPatch(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.GRASS.defaultBlockState(), 1).add(Blocks.FERN.defaultBlockState(), 4)), 32)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_GRASS = FeatureUtils.register("patch_grass", WorldGenerator.RANDOM_PATCH.configured(grassPatch(WorldGenFeatureStateProvider.simple(Blocks.GRASS), 32)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_GRASS_JUNGLE = FeatureUtils.register("patch_grass_jungle", WorldGenerator.RANDOM_PATCH.configured(new WorldGenFeatureRandomPatchConfiguration(32, 7, 3, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.GRASS.defaultBlockState(), 3).add(Blocks.FERN.defaultBlockState(), 1)))).filtered(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.not(BlockPredicate.matchesBlock(Blocks.PODZOL, new BlockPosition(0, -1, 0)))));
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureBlockConfiguration, ?> SINGLE_PIECE_OF_GRASS = FeatureUtils.register("single_piece_of_grass", WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.GRASS.defaultBlockState()))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_DEAD_BUSH = FeatureUtils.register("patch_dead_bush", WorldGenerator.RANDOM_PATCH.configured(grassPatch(WorldGenFeatureStateProvider.simple(Blocks.DEAD_BUSH), 4)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_MELON = FeatureUtils.register("patch_melon", WorldGenerator.RANDOM_PATCH.configured(new WorldGenFeatureRandomPatchConfiguration(64, 7, 3, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.MELON))).filtered(BlockPredicate.allOf(BlockPredicate.replaceable(), BlockPredicate.matchesBlock(Blocks.GRASS_BLOCK, new BlockPosition(0, -1, 0))));
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_WATERLILY = FeatureUtils.register("patch_waterlily", WorldGenerator.RANDOM_PATCH.configured(new WorldGenFeatureRandomPatchConfiguration(10, 7, 3, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LILY_PAD))).onlyWhenEmpty();
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_TALL_GRASS = FeatureUtils.register("patch_tall_grass", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.TALL_GRASS))))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_LARGE_FERN = FeatureUtils.register("patch_large_fern", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LARGE_FERN))))));
    public static final WorldGenFeatureConfigured<?, ?> PATCH_CACTUS = FeatureUtils.register("patch_cactus", WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simpleRandomPatchConfiguration(10, WorldGenerator.BLOCK_COLUMN.configured(BlockColumnConfiguration.simple(BiasedToBottomInt.of(1, 3), WorldGenFeatureStateProvider.simple(Blocks.CACTUS))).placed(BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(Blocks.CACTUS.defaultBlockState(), BlockPosition.ZERO)))))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> PATCH_SUGAR_CANE = FeatureUtils.register("patch_sugar_cane", WorldGenerator.RANDOM_PATCH.configured(new WorldGenFeatureRandomPatchConfiguration(20, 4, 0, () -> {
        return WorldGenerator.BLOCK_COLUMN.configured(BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), WorldGenFeatureStateProvider.simple(Blocks.SUGAR_CANE))).placed(BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlock(Blocks.AIR, BlockPosition.ZERO), BlockPredicate.wouldSurvive(Blocks.SUGAR_CANE.defaultBlockState(), BlockPosition.ZERO), BlockPredicate.anyOf(BlockPredicate.matchesFluids(List.of(FluidTypes.WATER, FluidTypes.FLOWING_WATER), new BlockPosition(1, -1, 0)), BlockPredicate.matchesFluids(List.of(FluidTypes.WATER, FluidTypes.FLOWING_WATER), new BlockPosition(-1, -1, 0)), BlockPredicate.matchesFluids(List.of(FluidTypes.WATER, FluidTypes.FLOWING_WATER), new BlockPosition(0, -1, 1)), BlockPredicate.matchesFluids(List.of(FluidTypes.WATER, FluidTypes.FLOWING_WATER), new BlockPosition(0, -1, -1))))));
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> FLOWER_DEFAULT = FeatureUtils.register("flower_default", WorldGenerator.FLOWER.configured(grassPatch(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.POPPY.defaultBlockState(), 2).add(Blocks.DANDELION.defaultBlockState(), 1)), 64)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> FLOWER_FLOWER_FOREST = FeatureUtils.register("flower_flower_forest", WorldGenerator.FLOWER.configured(new WorldGenFeatureRandomPatchConfiguration(96, 6, 2, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(new NoiseProvider(2345L, new NoiseGeneratorNormal.a(0, 1.0D, new double[0]), 0.020833334F, List.of(Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState())))).onlyWhenEmpty();
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> FLOWER_SWAMP = FeatureUtils.register("flower_swamp", WorldGenerator.FLOWER.configured(new WorldGenFeatureRandomPatchConfiguration(64, 6, 2, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.BLUE_ORCHID))).onlyWhenEmpty();
    })));
    public static final WorldGenFeatureConfigured<?, ?> FLOWER_PLAIN = FeatureUtils.register("flower_plain", WorldGenerator.FLOWER.configured(new WorldGenFeatureRandomPatchConfiguration(64, 6, 2, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(new NoiseThresholdProvider(2345L, new NoiseGeneratorNormal.a(0, 1.0D, new double[0]), 0.005F, -0.8F, 0.33333334F, Blocks.DANDELION.defaultBlockState(), List.of(Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState()), List.of(Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState())))).onlyWhenEmpty();
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomPatchConfiguration, ?> FLOWER_MEADOW = FeatureUtils.register("flower_meadow", WorldGenerator.FLOWER.configured(new WorldGenFeatureRandomPatchConfiguration(96, 6, 2, () -> {
        return WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(new DualNoiseProvider(new InclusiveRange<>(1, 3), new NoiseGeneratorNormal.a(-10, 1.0D, new double[0]), 1.0F, 2345L, new NoiseGeneratorNormal.a(-3, 1.0D, new double[0]), 1.0F, List.of(Blocks.TALL_GRASS.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.DANDELION.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.GRASS.defaultBlockState())))).onlyWhenEmpty();
    })));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandom2, ?> FOREST_FLOWERS = FeatureUtils.register("forest_flowers", WorldGenerator.SIMPLE_RANDOM_SELECTOR.configured(new WorldGenFeatureRandom2(List.of(() -> {
        return WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LILAC))))).placed();
    }, () -> {
        return WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.ROSE_BUSH))))).placed();
    }, () -> {
        return WorldGenerator.RANDOM_PATCH.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.PEONY))))).placed();
    }, () -> {
        return WorldGenerator.NO_BONEMEAL_FLOWER.configured(FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LILY_OF_THE_VALLEY))))).placed();
    }))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> DARK_FOREST_VEGETATION = FeatureUtils.register("dark_forest_vegetation", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreeFeatures.HUGE_BROWN_MUSHROOM.placed(), 0.025F), new WeightedPlacedFeature(TreeFeatures.HUGE_RED_MUSHROOM.placed(), 0.05F), new WeightedPlacedFeature(TreePlacements.DARK_OAK_CHECKED, 0.6666667F), new WeightedPlacedFeature(TreePlacements.BIRCH_CHECKED, 0.2F), new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.1F)), TreePlacements.OAK_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_FLOWER_FOREST = FeatureUtils.register("trees_flower_forest", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.BIRCH_BEES_002, 0.2F), new WeightedPlacedFeature(TreePlacements.FANCY_OAK_BEES_002, 0.1F)), TreePlacements.OAK_BEES_002)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> MEADOW_TREES = FeatureUtils.register("meadow_trees", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.FANCY_OAK_BEES, 0.5F)), TreePlacements.SUPER_BIRCH_BEES)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_TAIGA = FeatureUtils.register("trees_taiga", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.PINE_CHECKED, 0.33333334F)), TreePlacements.SPRUCE_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_GROVE = FeatureUtils.register("trees_grove", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.PINE_ON_SNOW, 0.33333334F)), TreePlacements.SPRUCE_ON_SNOW)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_SAVANNA = FeatureUtils.register("trees_savanna", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.ACACIA_CHECKED, 0.8F)), TreePlacements.OAK_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> BIRCH_TALL = FeatureUtils.register("birch_tall", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.SUPER_BIRCH_BEES_0002, 0.5F)), TreePlacements.BIRCH_BEES_0002_PLACED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_WINDSWEPT_HILLS = FeatureUtils.register("trees_windswept_hills", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.SPRUCE_CHECKED, 0.666F), new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.1F)), TreePlacements.OAK_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_WATER = FeatureUtils.register("trees_water", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.1F)), TreePlacements.OAK_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_BIRCH_AND_OAK = FeatureUtils.register("trees_birch_and_oak", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.BIRCH_BEES_0002_PLACED, 0.2F), new WeightedPlacedFeature(TreePlacements.FANCY_OAK_BEES_0002, 0.1F)), TreePlacements.OAK_BEES_0002)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_PLAINS = FeatureUtils.register("trees_plains", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreeFeatures.FANCY_OAK_BEES_005.placed(), 0.33333334F)), TreeFeatures.OAK_BEES_005.placed())));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_SPARSE_JUNGLE = FeatureUtils.register("trees_sparse_jungle", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.1F), new WeightedPlacedFeature(TreePlacements.JUNGLE_BUSH, 0.5F)), TreePlacements.JUNGLE_TREE_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_OLD_GROWTH_SPRUCE_TAIGA = FeatureUtils.register("trees_old_growth_spruce_taiga", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.MEGA_SPRUCE_CHECKED, 0.33333334F), new WeightedPlacedFeature(TreePlacements.PINE_CHECKED, 0.33333334F)), TreePlacements.SPRUCE_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_OLD_GROWTH_PINE_TAIGA = FeatureUtils.register("trees_old_growth_pine_taiga", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.MEGA_SPRUCE_CHECKED, 0.025641026F), new WeightedPlacedFeature(TreePlacements.MEGA_PINE_CHECKED, 0.30769232F), new WeightedPlacedFeature(TreePlacements.PINE_CHECKED, 0.33333334F)), TreePlacements.SPRUCE_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> TREES_JUNGLE = FeatureUtils.register("trees_jungle", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.1F), new WeightedPlacedFeature(TreePlacements.JUNGLE_BUSH, 0.5F), new WeightedPlacedFeature(TreePlacements.MEGA_JUNGLE_TREE_CHECKED, 0.33333334F)), TreePlacements.JUNGLE_TREE_CHECKED)));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandomChoiceConfiguration, ?> BAMBOO_VEGETATION = FeatureUtils.register("bamboo_vegetation", WorldGenerator.RANDOM_SELECTOR.configured(new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(TreePlacements.FANCY_OAK_CHECKED, 0.05F), new WeightedPlacedFeature(TreePlacements.JUNGLE_BUSH, 0.15F), new WeightedPlacedFeature(TreePlacements.MEGA_JUNGLE_TREE_CHECKED, 0.7F)), VegetationFeatures.PATCH_GRASS_JUNGLE.placed())));
    public static final WorldGenFeatureConfigured<WorldGenFeatureChoiceConfiguration, ?> MUSHROOM_ISLAND_VEGETATION = FeatureUtils.register("mushroom_island_vegetation", WorldGenerator.RANDOM_BOOLEAN_SELECTOR.configured(new WorldGenFeatureChoiceConfiguration(() -> {
        return TreeFeatures.HUGE_RED_MUSHROOM.placed();
    }, () -> {
        return TreeFeatures.HUGE_BROWN_MUSHROOM.placed();
    })));

    public VegetationFeatures() {}

    private static WorldGenFeatureRandomPatchConfiguration grassPatch(WorldGenFeatureStateProvider worldgenfeaturestateprovider, int i) {
        return FeatureUtils.simpleRandomPatchConfiguration(i, WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(worldgenfeaturestateprovider)).onlyWhenEmpty());
    }
}
