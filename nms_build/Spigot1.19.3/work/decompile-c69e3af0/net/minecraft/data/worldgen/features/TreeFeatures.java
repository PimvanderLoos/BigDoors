package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockHugeMushroom;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureHugeFungiConfiguration;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeThreeLayers;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeTwoLayers;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerAcacia;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerBlob;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerBush;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerDarkOak;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerFancy;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerJungle;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerMegaPine;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerPine;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacerSpruce;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.MangroveRootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTreeAlterGround;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTreeBeehive;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTreeCocoa;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTreeVineLeaves;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTreeVineTrunk;
import net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerDarkOak;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerFancy;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerForking;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerGiant;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerMegaJungle;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerStraight;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;

public class TreeFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> CRIMSON_FUNGUS = FeatureUtils.createKey("crimson_fungus");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> CRIMSON_FUNGUS_PLANTED = FeatureUtils.createKey("crimson_fungus_planted");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> WARPED_FUNGUS = FeatureUtils.createKey("warped_fungus");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> WARPED_FUNGUS_PLANTED = FeatureUtils.createKey("warped_fungus_planted");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> HUGE_BROWN_MUSHROOM = FeatureUtils.createKey("huge_brown_mushroom");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> HUGE_RED_MUSHROOM = FeatureUtils.createKey("huge_red_mushroom");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> OAK = FeatureUtils.createKey("oak");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> DARK_OAK = FeatureUtils.createKey("dark_oak");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BIRCH = FeatureUtils.createKey("birch");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> ACACIA = FeatureUtils.createKey("acacia");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SPRUCE = FeatureUtils.createKey("spruce");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PINE = FeatureUtils.createKey("pine");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> JUNGLE_TREE = FeatureUtils.createKey("jungle_tree");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FANCY_OAK = FeatureUtils.createKey("fancy_oak");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> JUNGLE_TREE_NO_VINE = FeatureUtils.createKey("jungle_tree_no_vine");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MEGA_JUNGLE_TREE = FeatureUtils.createKey("mega_jungle_tree");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MEGA_SPRUCE = FeatureUtils.createKey("mega_spruce");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MEGA_PINE = FeatureUtils.createKey("mega_pine");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SUPER_BIRCH_BEES_0002 = FeatureUtils.createKey("super_birch_bees_0002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SUPER_BIRCH_BEES = FeatureUtils.createKey("super_birch_bees");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SWAMP_OAK = FeatureUtils.createKey("swamp_oak");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> JUNGLE_BUSH = FeatureUtils.createKey("jungle_bush");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> AZALEA_TREE = FeatureUtils.createKey("azalea_tree");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> MANGROVE = FeatureUtils.createKey("mangrove");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> TALL_MANGROVE = FeatureUtils.createKey("tall_mangrove");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> OAK_BEES_0002 = FeatureUtils.createKey("oak_bees_0002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> OAK_BEES_002 = FeatureUtils.createKey("oak_bees_002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> OAK_BEES_005 = FeatureUtils.createKey("oak_bees_005");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BIRCH_BEES_0002 = FeatureUtils.createKey("birch_bees_0002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BIRCH_BEES_002 = FeatureUtils.createKey("birch_bees_002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> BIRCH_BEES_005 = FeatureUtils.createKey("birch_bees_005");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FANCY_OAK_BEES_0002 = FeatureUtils.createKey("fancy_oak_bees_0002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FANCY_OAK_BEES_002 = FeatureUtils.createKey("fancy_oak_bees_002");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FANCY_OAK_BEES_005 = FeatureUtils.createKey("fancy_oak_bees_005");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> FANCY_OAK_BEES = FeatureUtils.createKey("fancy_oak_bees");

    public TreeFeatures() {}

    private static WorldGenFeatureTreeConfiguration.a createStraightBlobTree(Block block, Block block1, int i, int j, int k, int l) {
        return new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(block), new TrunkPlacerStraight(i, j, k), WorldGenFeatureStateProvider.simple(block1), new WorldGenFoilagePlacerBlob(ConstantInt.of(l), ConstantInt.of(0), 3), new FeatureSizeTwoLayers(1, 0, 1));
    }

    private static WorldGenFeatureTreeConfiguration.a createOak() {
        return createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
    }

    private static WorldGenFeatureTreeConfiguration.a createBirch() {
        return createStraightBlobTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 0, 2).ignoreVines();
    }

    private static WorldGenFeatureTreeConfiguration.a createSuperBirch() {
        return createStraightBlobTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 6, 2).ignoreVines();
    }

    private static WorldGenFeatureTreeConfiguration.a createJungleTree() {
        return createStraightBlobTree(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, 4, 8, 0, 2);
    }

    private static WorldGenFeatureTreeConfiguration.a createFancyOak() {
        return (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.OAK_LOG), new TrunkPlacerFancy(3, 11, 0), WorldGenFeatureStateProvider.simple(Blocks.OAK_LEAVES), new WorldGenFoilagePlacerFancy(ConstantInt.of(2), ConstantInt.of(4), 4), new FeatureSizeTwoLayers(0, 0, 0, OptionalInt.of(4)))).ignoreVines();
    }

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        HolderGetter<Block> holdergetter = bootstapcontext.lookup(Registries.BLOCK);

        FeatureUtils.register(bootstapcontext, TreeFeatures.CRIMSON_FUNGUS, WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false));
        FeatureUtils.register(bootstapcontext, TreeFeatures.CRIMSON_FUNGUS_PLANTED, WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true));
        FeatureUtils.register(bootstapcontext, TreeFeatures.WARPED_FUNGUS, WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false));
        FeatureUtils.register(bootstapcontext, TreeFeatures.WARPED_FUNGUS_PLANTED, WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true));
        FeatureUtils.register(bootstapcontext, TreeFeatures.HUGE_BROWN_MUSHROOM, WorldGenerator.HUGE_BROWN_MUSHROOM, new WorldGenFeatureMushroomConfiguration(WorldGenFeatureStateProvider.simple((IBlockData)((IBlockData)Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(BlockHugeMushroom.UP, true)).setValue(BlockHugeMushroom.DOWN, false)), WorldGenFeatureStateProvider.simple((IBlockData)((IBlockData)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(BlockHugeMushroom.UP, false)).setValue(BlockHugeMushroom.DOWN, false)), 3));
        FeatureUtils.register(bootstapcontext, TreeFeatures.HUGE_RED_MUSHROOM, WorldGenerator.HUGE_RED_MUSHROOM, new WorldGenFeatureMushroomConfiguration(WorldGenFeatureStateProvider.simple((IBlockData)Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(BlockHugeMushroom.DOWN, false)), WorldGenFeatureStateProvider.simple((IBlockData)((IBlockData)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(BlockHugeMushroom.UP, false)).setValue(BlockHugeMushroom.DOWN, false)), 2));
        WorldGenFeatureTreeBeehive worldgenfeaturetreebeehive = new WorldGenFeatureTreeBeehive(0.002F);
        WorldGenFeatureTreeBeehive worldgenfeaturetreebeehive1 = new WorldGenFeatureTreeBeehive(0.01F);
        WorldGenFeatureTreeBeehive worldgenfeaturetreebeehive2 = new WorldGenFeatureTreeBeehive(0.02F);
        WorldGenFeatureTreeBeehive worldgenfeaturetreebeehive3 = new WorldGenFeatureTreeBeehive(0.05F);
        WorldGenFeatureTreeBeehive worldgenfeaturetreebeehive4 = new WorldGenFeatureTreeBeehive(1.0F);

        FeatureUtils.register(bootstapcontext, TreeFeatures.OAK, WorldGenerator.TREE, createOak().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.DARK_OAK, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.DARK_OAK_LOG), new TrunkPlacerDarkOak(6, 2, 1), WorldGenFeatureStateProvider.simple(Blocks.DARK_OAK_LEAVES), new WorldGenFoilagePlacerDarkOak(ConstantInt.of(0), ConstantInt.of(0)), new FeatureSizeThreeLayers(1, 1, 0, 1, 2, OptionalInt.empty()))).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.BIRCH, WorldGenerator.TREE, createBirch().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.ACACIA, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.ACACIA_LOG), new TrunkPlacerForking(5, 2, 2), WorldGenFeatureStateProvider.simple(Blocks.ACACIA_LEAVES), new WorldGenFoilagePlacerAcacia(ConstantInt.of(2), ConstantInt.of(0)), new FeatureSizeTwoLayers(1, 0, 2))).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.SPRUCE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerStraight(5, 2, 1), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerSpruce(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)), new FeatureSizeTwoLayers(2, 0, 2))).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.PINE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerStraight(6, 4, 0), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerPine(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4)), new FeatureSizeTwoLayers(2, 0, 2))).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.JUNGLE_TREE, WorldGenerator.TREE, createJungleTree().decorators(ImmutableList.of(new WorldGenFeatureTreeCocoa(0.2F), WorldGenFeatureTreeVineTrunk.INSTANCE, new WorldGenFeatureTreeVineLeaves(0.25F))).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.FANCY_OAK, WorldGenerator.TREE, createFancyOak().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.JUNGLE_TREE_NO_VINE, WorldGenerator.TREE, createJungleTree().ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.MEGA_JUNGLE_TREE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.JUNGLE_LOG), new TrunkPlacerMegaJungle(10, 2, 19), WorldGenFeatureStateProvider.simple(Blocks.JUNGLE_LEAVES), new WorldGenFoilagePlacerJungle(ConstantInt.of(2), ConstantInt.of(0), 2), new FeatureSizeTwoLayers(1, 1, 2))).decorators(ImmutableList.of(WorldGenFeatureTreeVineTrunk.INSTANCE, new WorldGenFeatureTreeVineLeaves(0.25F))).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.MEGA_SPRUCE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerGiant(13, 2, 14), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerMegaPine(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)), new FeatureSizeTwoLayers(1, 1, 2))).decorators(ImmutableList.of(new WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider.simple(Blocks.PODZOL)))).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.MEGA_PINE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerGiant(13, 2, 14), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerMegaPine(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 7)), new FeatureSizeTwoLayers(1, 1, 2))).decorators(ImmutableList.of(new WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider.simple(Blocks.PODZOL)))).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.SUPER_BIRCH_BEES_0002, WorldGenerator.TREE, createSuperBirch().decorators(ImmutableList.of(worldgenfeaturetreebeehive)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.SUPER_BIRCH_BEES, WorldGenerator.TREE, createSuperBirch().decorators(ImmutableList.of(worldgenfeaturetreebeehive4)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.SWAMP_OAK, WorldGenerator.TREE, createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(new WorldGenFeatureTreeVineLeaves(0.25F))).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.JUNGLE_BUSH, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.JUNGLE_LOG), new TrunkPlacerStraight(1, 0, 0), WorldGenFeatureStateProvider.simple(Blocks.OAK_LEAVES), new WorldGenFoilagePlacerBush(ConstantInt.of(2), ConstantInt.of(1), 2), new FeatureSizeTwoLayers(0, 0, 0))).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.AZALEA_TREE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.OAK_LOG), new BendingTrunkPlacer(4, 2, 0, 3, UniformInt.of(1, 2)), new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.AZALEA_LEAVES.defaultBlockState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 1)), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 50), new FeatureSizeTwoLayers(1, 0, 1))).dirt(WorldGenFeatureStateProvider.simple(Blocks.ROOTED_DIRT)).forceDirt().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.MANGROVE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LOG), new UpwardsBranchingTrunkPlacer(2, 1, 4, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), holdergetter.getOrThrow(TagsBlock.MANGROVE_LOGS_CAN_GROW_THROUGH)), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LEAVES), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70), Optional.of(new MangroveRootPlacer(UniformInt.of(1, 3), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_ROOTS), Optional.of(new AboveRootPlacement(WorldGenFeatureStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)), new MangroveRootPlacement(holdergetter.getOrThrow(TagsBlock.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)), WorldGenFeatureStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))), new FeatureSizeTwoLayers(2, 0, 2))).decorators(List.of(new WorldGenFeatureTreeVineLeaves(0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(WorldGenFeatureStateProvider.simple((IBlockData)Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(EnumDirection.DOWN)), worldgenfeaturetreebeehive1)).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.TALL_MANGROVE, WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LOG), new UpwardsBranchingTrunkPlacer(4, 1, 9, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1), holdergetter.getOrThrow(TagsBlock.MANGROVE_LOGS_CAN_GROW_THROUGH)), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LEAVES), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70), Optional.of(new MangroveRootPlacer(UniformInt.of(3, 7), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_ROOTS), Optional.of(new AboveRootPlacement(WorldGenFeatureStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)), new MangroveRootPlacement(holdergetter.getOrThrow(TagsBlock.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)), WorldGenFeatureStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))), new FeatureSizeTwoLayers(3, 0, 2))).decorators(List.of(new WorldGenFeatureTreeVineLeaves(0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(WorldGenFeatureStateProvider.simple((IBlockData)Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(EnumDirection.DOWN)), worldgenfeaturetreebeehive1)).ignoreVines().build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.OAK_BEES_0002, WorldGenerator.TREE, createOak().decorators(List.of(worldgenfeaturetreebeehive)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.OAK_BEES_002, WorldGenerator.TREE, createOak().decorators(List.of(worldgenfeaturetreebeehive2)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.OAK_BEES_005, WorldGenerator.TREE, createOak().decorators(List.of(worldgenfeaturetreebeehive3)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.BIRCH_BEES_0002, WorldGenerator.TREE, createBirch().decorators(List.of(worldgenfeaturetreebeehive)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.BIRCH_BEES_002, WorldGenerator.TREE, createBirch().decorators(List.of(worldgenfeaturetreebeehive2)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.BIRCH_BEES_005, WorldGenerator.TREE, createBirch().decorators(List.of(worldgenfeaturetreebeehive3)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.FANCY_OAK_BEES_0002, WorldGenerator.TREE, createFancyOak().decorators(List.of(worldgenfeaturetreebeehive)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.FANCY_OAK_BEES_002, WorldGenerator.TREE, createFancyOak().decorators(List.of(worldgenfeaturetreebeehive2)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.FANCY_OAK_BEES_005, WorldGenerator.TREE, createFancyOak().decorators(List.of(worldgenfeaturetreebeehive3)).build());
        FeatureUtils.register(bootstapcontext, TreeFeatures.FANCY_OAK_BEES, WorldGenerator.TREE, createFancyOak().decorators(List.of(worldgenfeaturetreebeehive4)).build());
    }
}
