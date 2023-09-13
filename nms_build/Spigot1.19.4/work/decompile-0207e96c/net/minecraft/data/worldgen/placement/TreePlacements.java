package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class TreePlacements {

    public static final ResourceKey<PlacedFeature> CRIMSON_FUNGI = PlacementUtils.createKey("crimson_fungi");
    public static final ResourceKey<PlacedFeature> WARPED_FUNGI = PlacementUtils.createKey("warped_fungi");
    public static final ResourceKey<PlacedFeature> OAK_CHECKED = PlacementUtils.createKey("oak_checked");
    public static final ResourceKey<PlacedFeature> DARK_OAK_CHECKED = PlacementUtils.createKey("dark_oak_checked");
    public static final ResourceKey<PlacedFeature> BIRCH_CHECKED = PlacementUtils.createKey("birch_checked");
    public static final ResourceKey<PlacedFeature> ACACIA_CHECKED = PlacementUtils.createKey("acacia_checked");
    public static final ResourceKey<PlacedFeature> SPRUCE_CHECKED = PlacementUtils.createKey("spruce_checked");
    public static final ResourceKey<PlacedFeature> MANGROVE_CHECKED = PlacementUtils.createKey("mangrove_checked");
    public static final ResourceKey<PlacedFeature> CHERRY_CHECKED = PlacementUtils.createKey("cherry_checked");
    public static final ResourceKey<PlacedFeature> PINE_ON_SNOW = PlacementUtils.createKey("pine_on_snow");
    public static final ResourceKey<PlacedFeature> SPRUCE_ON_SNOW = PlacementUtils.createKey("spruce_on_snow");
    public static final ResourceKey<PlacedFeature> PINE_CHECKED = PlacementUtils.createKey("pine_checked");
    public static final ResourceKey<PlacedFeature> JUNGLE_TREE_CHECKED = PlacementUtils.createKey("jungle_tree");
    public static final ResourceKey<PlacedFeature> FANCY_OAK_CHECKED = PlacementUtils.createKey("fancy_oak_checked");
    public static final ResourceKey<PlacedFeature> MEGA_JUNGLE_TREE_CHECKED = PlacementUtils.createKey("mega_jungle_tree_checked");
    public static final ResourceKey<PlacedFeature> MEGA_SPRUCE_CHECKED = PlacementUtils.createKey("mega_spruce_checked");
    public static final ResourceKey<PlacedFeature> MEGA_PINE_CHECKED = PlacementUtils.createKey("mega_pine_checked");
    public static final ResourceKey<PlacedFeature> TALL_MANGROVE_CHECKED = PlacementUtils.createKey("tall_mangrove_checked");
    public static final ResourceKey<PlacedFeature> JUNGLE_BUSH = PlacementUtils.createKey("jungle_bush");
    public static final ResourceKey<PlacedFeature> SUPER_BIRCH_BEES_0002 = PlacementUtils.createKey("super_birch_bees_0002");
    public static final ResourceKey<PlacedFeature> SUPER_BIRCH_BEES = PlacementUtils.createKey("super_birch_bees");
    public static final ResourceKey<PlacedFeature> OAK_BEES_0002 = PlacementUtils.createKey("oak_bees_0002");
    public static final ResourceKey<PlacedFeature> OAK_BEES_002 = PlacementUtils.createKey("oak_bees_002");
    public static final ResourceKey<PlacedFeature> BIRCH_BEES_0002_PLACED = PlacementUtils.createKey("birch_bees_0002");
    public static final ResourceKey<PlacedFeature> BIRCH_BEES_002 = PlacementUtils.createKey("birch_bees_002");
    public static final ResourceKey<PlacedFeature> FANCY_OAK_BEES_0002 = PlacementUtils.createKey("fancy_oak_bees_0002");
    public static final ResourceKey<PlacedFeature> FANCY_OAK_BEES_002 = PlacementUtils.createKey("fancy_oak_bees_002");
    public static final ResourceKey<PlacedFeature> FANCY_OAK_BEES = PlacementUtils.createKey("fancy_oak_bees");
    public static final ResourceKey<PlacedFeature> CHERRY_BEES_005 = PlacementUtils.createKey("cherry_bees_005");

    public TreePlacements() {}

    public static void bootstrap(BootstapContext<PlacedFeature> bootstapcontext) {
        HolderGetter<WorldGenFeatureConfigured<?, ?>> holdergetter = bootstapcontext.lookup(Registries.CONFIGURED_FEATURE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder = holdergetter.getOrThrow(TreeFeatures.CRIMSON_FUNGUS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder1 = holdergetter.getOrThrow(TreeFeatures.WARPED_FUNGUS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder2 = holdergetter.getOrThrow(TreeFeatures.OAK);
        Holder<WorldGenFeatureConfigured<?, ?>> holder3 = holdergetter.getOrThrow(TreeFeatures.DARK_OAK);
        Holder<WorldGenFeatureConfigured<?, ?>> holder4 = holdergetter.getOrThrow(TreeFeatures.BIRCH);
        Holder<WorldGenFeatureConfigured<?, ?>> holder5 = holdergetter.getOrThrow(TreeFeatures.ACACIA);
        Holder<WorldGenFeatureConfigured<?, ?>> holder6 = holdergetter.getOrThrow(TreeFeatures.SPRUCE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder7 = holdergetter.getOrThrow(TreeFeatures.MANGROVE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder8 = holdergetter.getOrThrow(TreeFeatures.CHERRY);
        Holder<WorldGenFeatureConfigured<?, ?>> holder9 = holdergetter.getOrThrow(TreeFeatures.PINE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder10 = holdergetter.getOrThrow(TreeFeatures.JUNGLE_TREE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder11 = holdergetter.getOrThrow(TreeFeatures.FANCY_OAK);
        Holder<WorldGenFeatureConfigured<?, ?>> holder12 = holdergetter.getOrThrow(TreeFeatures.MEGA_JUNGLE_TREE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder13 = holdergetter.getOrThrow(TreeFeatures.MEGA_SPRUCE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder14 = holdergetter.getOrThrow(TreeFeatures.MEGA_PINE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder15 = holdergetter.getOrThrow(TreeFeatures.TALL_MANGROVE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder16 = holdergetter.getOrThrow(TreeFeatures.JUNGLE_BUSH);
        Holder<WorldGenFeatureConfigured<?, ?>> holder17 = holdergetter.getOrThrow(TreeFeatures.SUPER_BIRCH_BEES_0002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder18 = holdergetter.getOrThrow(TreeFeatures.SUPER_BIRCH_BEES);
        Holder<WorldGenFeatureConfigured<?, ?>> holder19 = holdergetter.getOrThrow(TreeFeatures.OAK_BEES_0002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder20 = holdergetter.getOrThrow(TreeFeatures.OAK_BEES_002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder21 = holdergetter.getOrThrow(TreeFeatures.BIRCH_BEES_0002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder22 = holdergetter.getOrThrow(TreeFeatures.BIRCH_BEES_002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder23 = holdergetter.getOrThrow(TreeFeatures.FANCY_OAK_BEES_0002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder24 = holdergetter.getOrThrow(TreeFeatures.FANCY_OAK_BEES_002);
        Holder<WorldGenFeatureConfigured<?, ?>> holder25 = holdergetter.getOrThrow(TreeFeatures.FANCY_OAK_BEES);
        Holder<WorldGenFeatureConfigured<?, ?>> holder26 = holdergetter.getOrThrow(TreeFeatures.CHERRY_BEES_005);

        PlacementUtils.register(bootstapcontext, TreePlacements.CRIMSON_FUNGI, holder, CountOnEveryLayerPlacement.of(8), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, TreePlacements.WARPED_FUNGI, holder1, CountOnEveryLayerPlacement.of(8), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, TreePlacements.OAK_CHECKED, holder2, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.DARK_OAK_CHECKED, holder3, PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.BIRCH_CHECKED, holder4, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.ACACIA_CHECKED, holder5, PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.SPRUCE_CHECKED, holder6, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.MANGROVE_CHECKED, holder7, PlacementUtils.filteredByBlockSurvival(Blocks.MANGROVE_PROPAGULE));
        PlacementUtils.register(bootstapcontext, TreePlacements.CHERRY_CHECKED, holder8, PlacementUtils.filteredByBlockSurvival(Blocks.CHERRY_SAPLING));
        BlockPredicate blockpredicate = BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW);
        List<PlacementModifier> list = List.of(EnvironmentScanPlacement.scanningFor(EnumDirection.UP, BlockPredicate.not(BlockPredicate.matchesBlocks(Blocks.POWDER_SNOW)), 8), BlockPredicateFilter.forPredicate(blockpredicate));

        PlacementUtils.register(bootstapcontext, TreePlacements.PINE_ON_SNOW, holder9, list);
        PlacementUtils.register(bootstapcontext, TreePlacements.SPRUCE_ON_SNOW, holder6, list);
        PlacementUtils.register(bootstapcontext, TreePlacements.PINE_CHECKED, holder9, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.JUNGLE_TREE_CHECKED, holder10, PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.FANCY_OAK_CHECKED, holder11, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.MEGA_JUNGLE_TREE_CHECKED, holder12, PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.MEGA_SPRUCE_CHECKED, holder13, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.MEGA_PINE_CHECKED, holder14, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.TALL_MANGROVE_CHECKED, holder15, PlacementUtils.filteredByBlockSurvival(Blocks.MANGROVE_PROPAGULE));
        PlacementUtils.register(bootstapcontext, TreePlacements.JUNGLE_BUSH, holder16, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.SUPER_BIRCH_BEES_0002, holder17, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.SUPER_BIRCH_BEES, holder18, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.OAK_BEES_0002, holder19, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.OAK_BEES_002, holder20, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.BIRCH_BEES_0002_PLACED, holder21, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.BIRCH_BEES_002, holder22, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.FANCY_OAK_BEES_0002, holder23, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.FANCY_OAK_BEES_002, holder24, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.FANCY_OAK_BEES, holder25, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        PlacementUtils.register(bootstapcontext, TreePlacements.CHERRY_BEES_005, holder26, PlacementUtils.filteredByBlockSurvival(Blocks.CHERRY_SAPLING));
    }
}
