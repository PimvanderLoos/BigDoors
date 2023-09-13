package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
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

    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>> CRIMSON_FUNGUS = FeatureUtils.register("crimson_fungus", WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>> CRIMSON_FUNGUS_PLANTED = FeatureUtils.register("crimson_fungus_planted", WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>> WARPED_FUNGUS = FeatureUtils.register("warped_fungus", WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureHugeFungiConfiguration, ?>> WARPED_FUNGUS_PLANTED = FeatureUtils.register("warped_fungus_planted", WorldGenerator.HUGE_FUNGUS, new WorldGenFeatureHugeFungiConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureMushroomConfiguration, ?>> HUGE_BROWN_MUSHROOM = FeatureUtils.register("huge_brown_mushroom", WorldGenerator.HUGE_BROWN_MUSHROOM, new WorldGenFeatureMushroomConfiguration(WorldGenFeatureStateProvider.simple((IBlockData) ((IBlockData) Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(BlockHugeMushroom.UP, true)).setValue(BlockHugeMushroom.DOWN, false)), WorldGenFeatureStateProvider.simple((IBlockData) ((IBlockData) Blocks.MUSHROOM_STEM.defaultBlockState().setValue(BlockHugeMushroom.UP, false)).setValue(BlockHugeMushroom.DOWN, false)), 3));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureMushroomConfiguration, ?>> HUGE_RED_MUSHROOM = FeatureUtils.register("huge_red_mushroom", WorldGenerator.HUGE_RED_MUSHROOM, new WorldGenFeatureMushroomConfiguration(WorldGenFeatureStateProvider.simple((IBlockData) Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(BlockHugeMushroom.DOWN, false)), WorldGenFeatureStateProvider.simple((IBlockData) ((IBlockData) Blocks.MUSHROOM_STEM.defaultBlockState().setValue(BlockHugeMushroom.UP, false)).setValue(BlockHugeMushroom.DOWN, false)), 2));
    private static final WorldGenFeatureTreeBeehive BEEHIVE_0002 = new WorldGenFeatureTreeBeehive(0.002F);
    private static final WorldGenFeatureTreeBeehive BEEHIVE_001 = new WorldGenFeatureTreeBeehive(0.01F);
    private static final WorldGenFeatureTreeBeehive BEEHIVE_002 = new WorldGenFeatureTreeBeehive(0.02F);
    private static final WorldGenFeatureTreeBeehive BEEHIVE_005 = new WorldGenFeatureTreeBeehive(0.05F);
    private static final WorldGenFeatureTreeBeehive BEEHIVE = new WorldGenFeatureTreeBeehive(1.0F);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> OAK = FeatureUtils.register("oak", WorldGenerator.TREE, createOak().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> DARK_OAK = FeatureUtils.register("dark_oak", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.DARK_OAK_LOG), new TrunkPlacerDarkOak(6, 2, 1), WorldGenFeatureStateProvider.simple(Blocks.DARK_OAK_LEAVES), new WorldGenFoilagePlacerDarkOak(ConstantInt.of(0), ConstantInt.of(0)), new FeatureSizeThreeLayers(1, 1, 0, 1, 2, OptionalInt.empty()))).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> BIRCH = FeatureUtils.register("birch", WorldGenerator.TREE, createBirch().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> ACACIA = FeatureUtils.register("acacia", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.ACACIA_LOG), new TrunkPlacerForking(5, 2, 2), WorldGenFeatureStateProvider.simple(Blocks.ACACIA_LEAVES), new WorldGenFoilagePlacerAcacia(ConstantInt.of(2), ConstantInt.of(0)), new FeatureSizeTwoLayers(1, 0, 2))).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> SPRUCE = FeatureUtils.register("spruce", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerStraight(5, 2, 1), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerSpruce(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)), new FeatureSizeTwoLayers(2, 0, 2))).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> PINE = FeatureUtils.register("pine", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerStraight(6, 4, 0), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerPine(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(3, 4)), new FeatureSizeTwoLayers(2, 0, 2))).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> JUNGLE_TREE = FeatureUtils.register("jungle_tree", WorldGenerator.TREE, createJungleTree().decorators(ImmutableList.of(new WorldGenFeatureTreeCocoa(0.2F), WorldGenFeatureTreeVineTrunk.INSTANCE, new WorldGenFeatureTreeVineLeaves(0.25F))).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> FANCY_OAK = FeatureUtils.register("fancy_oak", WorldGenerator.TREE, createFancyOak().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> JUNGLE_TREE_NO_VINE = FeatureUtils.register("jungle_tree_no_vine", WorldGenerator.TREE, createJungleTree().ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> MEGA_JUNGLE_TREE = FeatureUtils.register("mega_jungle_tree", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.JUNGLE_LOG), new TrunkPlacerMegaJungle(10, 2, 19), WorldGenFeatureStateProvider.simple(Blocks.JUNGLE_LEAVES), new WorldGenFoilagePlacerJungle(ConstantInt.of(2), ConstantInt.of(0), 2), new FeatureSizeTwoLayers(1, 1, 2))).decorators(ImmutableList.of(WorldGenFeatureTreeVineTrunk.INSTANCE, new WorldGenFeatureTreeVineLeaves(0.25F))).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> MEGA_SPRUCE = FeatureUtils.register("mega_spruce", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerGiant(13, 2, 14), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerMegaPine(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)), new FeatureSizeTwoLayers(1, 1, 2))).decorators(ImmutableList.of(new WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider.simple(Blocks.PODZOL)))).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> MEGA_PINE = FeatureUtils.register("mega_pine", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LOG), new TrunkPlacerGiant(13, 2, 14), WorldGenFeatureStateProvider.simple(Blocks.SPRUCE_LEAVES), new WorldGenFoilagePlacerMegaPine(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 7)), new FeatureSizeTwoLayers(1, 1, 2))).decorators(ImmutableList.of(new WorldGenFeatureTreeAlterGround(WorldGenFeatureStateProvider.simple(Blocks.PODZOL)))).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> SUPER_BIRCH_BEES_0002 = FeatureUtils.register("super_birch_bees_0002", WorldGenerator.TREE, createSuperBirch().decorators(ImmutableList.of(TreeFeatures.BEEHIVE_0002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> SUPER_BIRCH_BEES = FeatureUtils.register("super_birch_bees", WorldGenerator.TREE, createSuperBirch().decorators(ImmutableList.of(TreeFeatures.BEEHIVE)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> SWAMP_OAK = FeatureUtils.register("swamp_oak", WorldGenerator.TREE, createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(new WorldGenFeatureTreeVineLeaves(0.25F))).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> JUNGLE_BUSH = FeatureUtils.register("jungle_bush", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.JUNGLE_LOG), new TrunkPlacerStraight(1, 0, 0), WorldGenFeatureStateProvider.simple(Blocks.OAK_LEAVES), new WorldGenFoilagePlacerBush(ConstantInt.of(2), ConstantInt.of(1), 2), new FeatureSizeTwoLayers(0, 0, 0))).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> AZALEA_TREE = FeatureUtils.register("azalea_tree", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.OAK_LOG), new BendingTrunkPlacer(4, 2, 0, 3, UniformInt.of(1, 2)), new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.AZALEA_LEAVES.defaultBlockState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 1)), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 50), new FeatureSizeTwoLayers(1, 0, 1))).dirt(WorldGenFeatureStateProvider.simple(Blocks.ROOTED_DIRT)).forceDirt().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> MANGROVE = FeatureUtils.register("mangrove", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LOG), new UpwardsBranchingTrunkPlacer(2, 1, 4, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), IRegistry.BLOCK.getOrCreateTag(TagsBlock.MANGROVE_LOGS_CAN_GROW_THROUGH)), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LEAVES), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70), Optional.of(new MangroveRootPlacer(UniformInt.of(1, 3), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_ROOTS), Optional.of(new AboveRootPlacement(WorldGenFeatureStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)), new MangroveRootPlacement(IRegistry.BLOCK.getOrCreateTag(TagsBlock.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)), WorldGenFeatureStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))), new FeatureSizeTwoLayers(2, 0, 2))).decorators(List.of(new WorldGenFeatureTreeVineLeaves(0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(WorldGenFeatureStateProvider.simple((IBlockData)Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(EnumDirection.DOWN)), TreeFeatures.BEEHIVE_001)).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> TALL_MANGROVE = FeatureUtils.register("tall_mangrove", WorldGenerator.TREE, (new WorldGenFeatureTreeConfiguration.a(WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LOG), new UpwardsBranchingTrunkPlacer(4, 1, 9, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1), IRegistry.BLOCK.getOrCreateTag(TagsBlock.MANGROVE_LOGS_CAN_GROW_THROUGH)), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_LEAVES), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70), Optional.of(new MangroveRootPlacer(UniformInt.of(3, 7), WorldGenFeatureStateProvider.simple(Blocks.MANGROVE_ROOTS), Optional.of(new AboveRootPlacement(WorldGenFeatureStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)), new MangroveRootPlacement(IRegistry.BLOCK.getOrCreateTag(TagsBlock.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, (Object[])(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)), WorldGenFeatureStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS), 8, 15, 0.2F))), new FeatureSizeTwoLayers(3, 0, 2))).decorators(List.of(new WorldGenFeatureTreeVineLeaves(0.125F), new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(WorldGenFeatureStateProvider.simple((IBlockData)Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(EnumDirection.DOWN)), TreeFeatures.BEEHIVE_001)).ignoreVines().build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> OAK_BEES_0002 = FeatureUtils.register("oak_bees_0002", WorldGenerator.TREE, createOak().decorators(List.of(TreeFeatures.BEEHIVE_0002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> OAK_BEES_002 = FeatureUtils.register("oak_bees_002", WorldGenerator.TREE, createOak().decorators(List.of(TreeFeatures.BEEHIVE_002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> OAK_BEES_005 = FeatureUtils.register("oak_bees_005", WorldGenerator.TREE, createOak().decorators(List.of(TreeFeatures.BEEHIVE_005)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> BIRCH_BEES_0002 = FeatureUtils.register("birch_bees_0002", WorldGenerator.TREE, createBirch().decorators(List.of(TreeFeatures.BEEHIVE_0002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> BIRCH_BEES_002 = FeatureUtils.register("birch_bees_002", WorldGenerator.TREE, createBirch().decorators(List.of(TreeFeatures.BEEHIVE_002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> BIRCH_BEES_005 = FeatureUtils.register("birch_bees_005", WorldGenerator.TREE, createBirch().decorators(List.of(TreeFeatures.BEEHIVE_005)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> FANCY_OAK_BEES_0002 = FeatureUtils.register("fancy_oak_bees_0002", WorldGenerator.TREE, createFancyOak().decorators(List.of(TreeFeatures.BEEHIVE_0002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> FANCY_OAK_BEES_002 = FeatureUtils.register("fancy_oak_bees_002", WorldGenerator.TREE, createFancyOak().decorators(List.of(TreeFeatures.BEEHIVE_002)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> FANCY_OAK_BEES_005 = FeatureUtils.register("fancy_oak_bees_005", WorldGenerator.TREE, createFancyOak().decorators(List.of(TreeFeatures.BEEHIVE_005)).build());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?>> FANCY_OAK_BEES = FeatureUtils.register("fancy_oak_bees", WorldGenerator.TREE, createFancyOak().decorators(List.of(TreeFeatures.BEEHIVE)).build());

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
}
