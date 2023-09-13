package net.minecraft.data.worldgen.features;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.block.BlockSweetBerryBush;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomChoiceConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.DualNoiseProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import net.minecraft.world.level.material.FluidTypes;

public class VegetationFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BAMBOO_NO_PODZOL = FeatureUtils.createKey("bamboo_no_podzol");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BAMBOO_SOME_PODZOL = FeatureUtils.createKey("bamboo_some_podzol");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> VINES = FeatureUtils.createKey("vines");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_BROWN_MUSHROOM = FeatureUtils.createKey("patch_brown_mushroom");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_RED_MUSHROOM = FeatureUtils.createKey("patch_red_mushroom");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_SUNFLOWER = FeatureUtils.createKey("patch_sunflower");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_PUMPKIN = FeatureUtils.createKey("patch_pumpkin");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_BERRY_BUSH = FeatureUtils.createKey("patch_berry_bush");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_TAIGA_GRASS = FeatureUtils.createKey("patch_taiga_grass");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_GRASS = FeatureUtils.createKey("patch_grass");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_GRASS_JUNGLE = FeatureUtils.createKey("patch_grass_jungle");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SINGLE_PIECE_OF_GRASS = FeatureUtils.createKey("single_piece_of_grass");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_DEAD_BUSH = FeatureUtils.createKey("patch_dead_bush");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_MELON = FeatureUtils.createKey("patch_melon");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_WATERLILY = FeatureUtils.createKey("patch_waterlily");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_TALL_GRASS = FeatureUtils.createKey("patch_tall_grass");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_LARGE_FERN = FeatureUtils.createKey("patch_large_fern");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_CACTUS = FeatureUtils.createKey("patch_cactus");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PATCH_SUGAR_CANE = FeatureUtils.createKey("patch_sugar_cane");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FLOWER_DEFAULT = FeatureUtils.createKey("flower_default");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FLOWER_FLOWER_FOREST = FeatureUtils.createKey("flower_flower_forest");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FLOWER_SWAMP = FeatureUtils.createKey("flower_swamp");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FLOWER_PLAIN = FeatureUtils.createKey("flower_plain");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FLOWER_MEADOW = FeatureUtils.createKey("flower_meadow");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FLOWER_CHERRY = FeatureUtils.createKey("flower_cherry");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FOREST_FLOWERS = FeatureUtils.createKey("forest_flowers");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DARK_FOREST_VEGETATION = FeatureUtils.createKey("dark_forest_vegetation");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_FLOWER_FOREST = FeatureUtils.createKey("trees_flower_forest");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MEADOW_TREES = FeatureUtils.createKey("meadow_trees");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_TAIGA = FeatureUtils.createKey("trees_taiga");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_GROVE = FeatureUtils.createKey("trees_grove");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_SAVANNA = FeatureUtils.createKey("trees_savanna");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BIRCH_TALL = FeatureUtils.createKey("birch_tall");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_WINDSWEPT_HILLS = FeatureUtils.createKey("trees_windswept_hills");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_WATER = FeatureUtils.createKey("trees_water");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_BIRCH_AND_OAK = FeatureUtils.createKey("trees_birch_and_oak");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_PLAINS = FeatureUtils.createKey("trees_plains");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_SPARSE_JUNGLE = FeatureUtils.createKey("trees_sparse_jungle");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_OLD_GROWTH_SPRUCE_TAIGA = FeatureUtils.createKey("trees_old_growth_spruce_taiga");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_OLD_GROWTH_PINE_TAIGA = FeatureUtils.createKey("trees_old_growth_pine_taiga");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TREES_JUNGLE = FeatureUtils.createKey("trees_jungle");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BAMBOO_VEGETATION = FeatureUtils.createKey("bamboo_vegetation");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MUSHROOM_ISLAND_VEGETATION = FeatureUtils.createKey("mushroom_island_vegetation");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MANGROVE_VEGETATION = FeatureUtils.createKey("mangrove_vegetation");

    public VegetationFeatures() {}

    private static WorldGenFeatureRandomPatchConfiguration grassPatch(WorldGenFeatureStateProvider worldgenfeaturestateprovider, int i) {
        return FeatureUtils.simpleRandomPatchConfiguration(i, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(worldgenfeaturestateprovider)));
    }

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        HolderGetter<WorldGenFeatureConfigured<?, ?>> holdergetter = bootstapcontext.lookup(Registries.CONFIGURED_FEATURE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder = holdergetter.getOrThrow(TreeFeatures.HUGE_BROWN_MUSHROOM);
        Holder<WorldGenFeatureConfigured<?, ?>> holder1 = holdergetter.getOrThrow(TreeFeatures.HUGE_RED_MUSHROOM);
        Holder<WorldGenFeatureConfigured<?, ?>> holder2 = holdergetter.getOrThrow(TreeFeatures.FANCY_OAK_BEES_005);
        Holder<WorldGenFeatureConfigured<?, ?>> holder3 = holdergetter.getOrThrow(TreeFeatures.OAK_BEES_005);
        Holder<WorldGenFeatureConfigured<?, ?>> holder4 = holdergetter.getOrThrow(VegetationFeatures.PATCH_GRASS_JUNGLE);
        HolderGetter<PlacedFeature> holdergetter1 = bootstapcontext.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> holder5 = holdergetter1.getOrThrow(TreePlacements.DARK_OAK_CHECKED);
        Holder<PlacedFeature> holder6 = holdergetter1.getOrThrow(TreePlacements.BIRCH_CHECKED);
        Holder<PlacedFeature> holder7 = holdergetter1.getOrThrow(TreePlacements.FANCY_OAK_CHECKED);
        Holder<PlacedFeature> holder8 = holdergetter1.getOrThrow(TreePlacements.BIRCH_BEES_002);
        Holder<PlacedFeature> holder9 = holdergetter1.getOrThrow(TreePlacements.FANCY_OAK_BEES_002);
        Holder<PlacedFeature> holder10 = holdergetter1.getOrThrow(TreePlacements.FANCY_OAK_BEES);
        Holder<PlacedFeature> holder11 = holdergetter1.getOrThrow(TreePlacements.PINE_CHECKED);
        Holder<PlacedFeature> holder12 = holdergetter1.getOrThrow(TreePlacements.SPRUCE_CHECKED);
        Holder<PlacedFeature> holder13 = holdergetter1.getOrThrow(TreePlacements.PINE_ON_SNOW);
        Holder<PlacedFeature> holder14 = holdergetter1.getOrThrow(TreePlacements.ACACIA_CHECKED);
        Holder<PlacedFeature> holder15 = holdergetter1.getOrThrow(TreePlacements.SUPER_BIRCH_BEES_0002);
        Holder<PlacedFeature> holder16 = holdergetter1.getOrThrow(TreePlacements.BIRCH_BEES_0002_PLACED);
        Holder<PlacedFeature> holder17 = holdergetter1.getOrThrow(TreePlacements.FANCY_OAK_BEES_0002);
        Holder<PlacedFeature> holder18 = holdergetter1.getOrThrow(TreePlacements.JUNGLE_BUSH);
        Holder<PlacedFeature> holder19 = holdergetter1.getOrThrow(TreePlacements.MEGA_SPRUCE_CHECKED);
        Holder<PlacedFeature> holder20 = holdergetter1.getOrThrow(TreePlacements.MEGA_PINE_CHECKED);
        Holder<PlacedFeature> holder21 = holdergetter1.getOrThrow(TreePlacements.MEGA_JUNGLE_TREE_CHECKED);
        Holder<PlacedFeature> holder22 = holdergetter1.getOrThrow(TreePlacements.TALL_MANGROVE_CHECKED);
        Holder<PlacedFeature> holder23 = holdergetter1.getOrThrow(TreePlacements.OAK_CHECKED);
        Holder<PlacedFeature> holder24 = holdergetter1.getOrThrow(TreePlacements.OAK_BEES_002);
        Holder<PlacedFeature> holder25 = holdergetter1.getOrThrow(TreePlacements.SUPER_BIRCH_BEES);
        Holder<PlacedFeature> holder26 = holdergetter1.getOrThrow(TreePlacements.SPRUCE_ON_SNOW);
        Holder<PlacedFeature> holder27 = holdergetter1.getOrThrow(TreePlacements.OAK_BEES_0002);
        Holder<PlacedFeature> holder28 = holdergetter1.getOrThrow(TreePlacements.JUNGLE_TREE_CHECKED);
        Holder<PlacedFeature> holder29 = holdergetter1.getOrThrow(TreePlacements.MANGROVE_CHECKED);

        FeatureUtils.register(bootstapcontext, VegetationFeatures.BAMBOO_NO_PODZOL, WorldGenerator.BAMBOO, new WorldGenFeatureConfigurationChance(0.0F));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.BAMBOO_SOME_PODZOL, WorldGenerator.BAMBOO, new WorldGenFeatureConfigurationChance(0.2F));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.VINES, WorldGenerator.VINES);
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_BROWN_MUSHROOM, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.BROWN_MUSHROOM))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_RED_MUSHROOM, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.RED_MUSHROOM))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_SUNFLOWER, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SUNFLOWER))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_PUMPKIN, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.PUMPKIN)), List.of(Blocks.GRASS_BLOCK)));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_BERRY_BUSH, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple((IBlockData) Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(BlockSweetBerryBush.AGE, 3))), List.of(Blocks.GRASS_BLOCK)));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_TAIGA_GRASS, WorldGenerator.RANDOM_PATCH, grassPatch(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.GRASS.defaultBlockState(), 1).add(Blocks.FERN.defaultBlockState(), 4)), 32));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_GRASS, WorldGenerator.RANDOM_PATCH, grassPatch(WorldGenFeatureStateProvider.simple(Blocks.GRASS), 32));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_GRASS_JUNGLE, WorldGenerator.RANDOM_PATCH, new WorldGenFeatureRandomPatchConfiguration(32, 7, 3, PlacementUtils.filtered(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.GRASS.defaultBlockState(), 3).add(Blocks.FERN.defaultBlockState(), 1))), BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.not(BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), Blocks.PODZOL))))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.SINGLE_PIECE_OF_GRASS, WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.GRASS.defaultBlockState())));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_DEAD_BUSH, WorldGenerator.RANDOM_PATCH, grassPatch(WorldGenFeatureStateProvider.simple(Blocks.DEAD_BUSH), 4));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_MELON, WorldGenerator.RANDOM_PATCH, new WorldGenFeatureRandomPatchConfiguration(64, 7, 3, PlacementUtils.filtered(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.MELON)), BlockPredicate.allOf(BlockPredicate.replaceable(), BlockPredicate.noFluid(), BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), Blocks.GRASS_BLOCK)))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_WATERLILY, WorldGenerator.RANDOM_PATCH, new WorldGenFeatureRandomPatchConfiguration(10, 7, 3, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LILY_PAD)))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_TALL_GRASS, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.TALL_GRASS))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_LARGE_FERN, WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LARGE_FERN))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_CACTUS, WorldGenerator.RANDOM_PATCH, FeatureUtils.simpleRandomPatchConfiguration(10, PlacementUtils.inlinePlaced(WorldGenerator.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(1, 3), WorldGenFeatureStateProvider.simple(Blocks.CACTUS)), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(Blocks.CACTUS.defaultBlockState(), BlockPosition.ZERO))))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.PATCH_SUGAR_CANE, WorldGenerator.RANDOM_PATCH, new WorldGenFeatureRandomPatchConfiguration(20, 4, 0, PlacementUtils.inlinePlaced(WorldGenerator.BLOCK_COLUMN, BlockColumnConfiguration.simple(BiasedToBottomInt.of(2, 4), WorldGenFeatureStateProvider.simple(Blocks.SUGAR_CANE)), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.wouldSurvive(Blocks.SUGAR_CANE.defaultBlockState(), BlockPosition.ZERO), BlockPredicate.anyOf(BlockPredicate.matchesFluids(new BlockPosition(1, -1, 0), FluidTypes.WATER, FluidTypes.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPosition(-1, -1, 0), FluidTypes.WATER, FluidTypes.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPosition(0, -1, 1), FluidTypes.WATER, FluidTypes.FLOWING_WATER), BlockPredicate.matchesFluids(new BlockPosition(0, -1, -1), FluidTypes.WATER, FluidTypes.FLOWING_WATER)))))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.FLOWER_DEFAULT, WorldGenerator.FLOWER, grassPatch(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.POPPY.defaultBlockState(), 2).add(Blocks.DANDELION.defaultBlockState(), 1)), 64));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.FLOWER_FLOWER_FOREST, WorldGenerator.FLOWER, new WorldGenFeatureRandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(new NoiseProvider(2345L, new NoiseGeneratorNormal.a(0, 1.0D, new double[0]), 0.020833334F, List.of(Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()))))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.FLOWER_SWAMP, WorldGenerator.FLOWER, new WorldGenFeatureRandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.BLUE_ORCHID)))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.FLOWER_PLAIN, WorldGenerator.FLOWER, new WorldGenFeatureRandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(new NoiseThresholdProvider(2345L, new NoiseGeneratorNormal.a(0, 1.0D, new double[0]), 0.005F, -0.8F, 0.33333334F, Blocks.DANDELION.defaultBlockState(), List.of(Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState()), List.of(Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState()))))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.FLOWER_MEADOW, WorldGenerator.FLOWER, new WorldGenFeatureRandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(new DualNoiseProvider(new InclusiveRange<>(1, 3), new NoiseGeneratorNormal.a(-10, 1.0D, new double[0]), 1.0F, 2345L, new NoiseGeneratorNormal.a(-3, 1.0D, new double[0]), 1.0F, List.of(Blocks.TALL_GRASS.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.DANDELION.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.GRASS.defaultBlockState()))))));
        SimpleWeightedRandomList.a<IBlockData> simpleweightedrandomlist_a = SimpleWeightedRandomList.builder();

        for (int i = 1; i <= 4; ++i) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                simpleweightedrandomlist_a.add((IBlockData) ((IBlockData) Blocks.PINK_PETALS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, i)).setValue(PinkPetalsBlock.FACING, enumdirection), 1);
            }
        }

        FeatureUtils.register(bootstapcontext, VegetationFeatures.FLOWER_CHERRY, WorldGenerator.FLOWER, new WorldGenFeatureRandomPatchConfiguration(96, 6, 2, PlacementUtils.onlyWhenEmpty(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(new WorldGenFeatureStateProviderWeighted(simpleweightedrandomlist_a)))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.FOREST_FLOWERS, WorldGenerator.SIMPLE_RANDOM_SELECTOR, new WorldGenFeatureRandom2(HolderSet.direct(PlacementUtils.inlinePlaced(WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LILAC)))), PlacementUtils.inlinePlaced(WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.ROSE_BUSH)))), PlacementUtils.inlinePlaced(WorldGenerator.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.PEONY)))), PlacementUtils.inlinePlaced(WorldGenerator.NO_BONEMEAL_FLOWER, FeatureUtils.simplePatchConfiguration(WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.LILY_OF_THE_VALLEY)))))));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.DARK_FOREST_VEGETATION, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(holder), 0.025F), new WeightedPlacedFeature(PlacementUtils.inlinePlaced(holder1), 0.05F), new WeightedPlacedFeature(holder5, 0.6666667F), new WeightedPlacedFeature(holder6, 0.2F), new WeightedPlacedFeature(holder7, 0.1F)), holder23));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_FLOWER_FOREST, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder8, 0.2F), new WeightedPlacedFeature(holder9, 0.1F)), holder24));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.MEADOW_TREES, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder10, 0.5F)), holder25));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_TAIGA, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder11, 0.33333334F)), holder12));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_GROVE, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder13, 0.33333334F)), holder26));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_SAVANNA, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder14, 0.8F)), holder23));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.BIRCH_TALL, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder15, 0.5F)), holder16));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_WINDSWEPT_HILLS, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder12, 0.666F), new WeightedPlacedFeature(holder7, 0.1F)), holder23));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_WATER, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder7, 0.1F)), holder23));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_BIRCH_AND_OAK, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder16, 0.2F), new WeightedPlacedFeature(holder17, 0.1F)), holder27));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_PLAINS, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(holder2), 0.33333334F)), PlacementUtils.inlinePlaced(holder3)));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_SPARSE_JUNGLE, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder7, 0.1F), new WeightedPlacedFeature(holder18, 0.5F)), holder28));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder19, 0.33333334F), new WeightedPlacedFeature(holder11, 0.33333334F)), holder12));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder19, 0.025641026F), new WeightedPlacedFeature(holder20, 0.30769232F), new WeightedPlacedFeature(holder11, 0.33333334F)), holder12));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.TREES_JUNGLE, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder7, 0.1F), new WeightedPlacedFeature(holder18, 0.5F), new WeightedPlacedFeature(holder21, 0.33333334F)), holder28));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.BAMBOO_VEGETATION, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder7, 0.05F), new WeightedPlacedFeature(holder18, 0.15F), new WeightedPlacedFeature(holder21, 0.7F)), PlacementUtils.inlinePlaced(holder4)));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.MUSHROOM_ISLAND_VEGETATION, WorldGenerator.RANDOM_BOOLEAN_SELECTOR, new WorldGenFeatureChoiceConfiguration(PlacementUtils.inlinePlaced(holder1), PlacementUtils.inlinePlaced(holder)));
        FeatureUtils.register(bootstapcontext, VegetationFeatures.MANGROVE_VEGETATION, WorldGenerator.RANDOM_SELECTOR, new WorldGenFeatureRandomChoiceConfiguration(List.of(new WeightedPlacedFeature(holder22, 0.85F)), holder29));
    }
}
