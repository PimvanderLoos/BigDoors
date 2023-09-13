package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class NetherPlacements {

    public static final ResourceKey<PlacedFeature> DELTA = PlacementUtils.createKey("delta");
    public static final ResourceKey<PlacedFeature> SMALL_BASALT_COLUMNS = PlacementUtils.createKey("small_basalt_columns");
    public static final ResourceKey<PlacedFeature> LARGE_BASALT_COLUMNS = PlacementUtils.createKey("large_basalt_columns");
    public static final ResourceKey<PlacedFeature> BASALT_BLOBS = PlacementUtils.createKey("basalt_blobs");
    public static final ResourceKey<PlacedFeature> BLACKSTONE_BLOBS = PlacementUtils.createKey("blackstone_blobs");
    public static final ResourceKey<PlacedFeature> GLOWSTONE_EXTRA = PlacementUtils.createKey("glowstone_extra");
    public static final ResourceKey<PlacedFeature> GLOWSTONE = PlacementUtils.createKey("glowstone");
    public static final ResourceKey<PlacedFeature> CRIMSON_FOREST_VEGETATION = PlacementUtils.createKey("crimson_forest_vegetation");
    public static final ResourceKey<PlacedFeature> WARPED_FOREST_VEGETATION = PlacementUtils.createKey("warped_forest_vegetation");
    public static final ResourceKey<PlacedFeature> NETHER_SPROUTS = PlacementUtils.createKey("nether_sprouts");
    public static final ResourceKey<PlacedFeature> TWISTING_VINES = PlacementUtils.createKey("twisting_vines");
    public static final ResourceKey<PlacedFeature> WEEPING_VINES = PlacementUtils.createKey("weeping_vines");
    public static final ResourceKey<PlacedFeature> PATCH_CRIMSON_ROOTS = PlacementUtils.createKey("patch_crimson_roots");
    public static final ResourceKey<PlacedFeature> BASALT_PILLAR = PlacementUtils.createKey("basalt_pillar");
    public static final ResourceKey<PlacedFeature> SPRING_DELTA = PlacementUtils.createKey("spring_delta");
    public static final ResourceKey<PlacedFeature> SPRING_CLOSED = PlacementUtils.createKey("spring_closed");
    public static final ResourceKey<PlacedFeature> SPRING_CLOSED_DOUBLE = PlacementUtils.createKey("spring_closed_double");
    public static final ResourceKey<PlacedFeature> SPRING_OPEN = PlacementUtils.createKey("spring_open");
    public static final ResourceKey<PlacedFeature> PATCH_SOUL_FIRE = PlacementUtils.createKey("patch_soul_fire");
    public static final ResourceKey<PlacedFeature> PATCH_FIRE = PlacementUtils.createKey("patch_fire");

    public NetherPlacements() {}

    public static void bootstrap(BootstapContext<PlacedFeature> bootstapcontext) {
        HolderGetter<WorldGenFeatureConfigured<?, ?>> holdergetter = bootstapcontext.lookup(Registries.CONFIGURED_FEATURE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder = holdergetter.getOrThrow(NetherFeatures.DELTA);
        Holder<WorldGenFeatureConfigured<?, ?>> holder1 = holdergetter.getOrThrow(NetherFeatures.SMALL_BASALT_COLUMNS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder2 = holdergetter.getOrThrow(NetherFeatures.LARGE_BASALT_COLUMNS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder3 = holdergetter.getOrThrow(NetherFeatures.BASALT_BLOBS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder4 = holdergetter.getOrThrow(NetherFeatures.BLACKSTONE_BLOBS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder5 = holdergetter.getOrThrow(NetherFeatures.GLOWSTONE_EXTRA);
        Holder<WorldGenFeatureConfigured<?, ?>> holder6 = holdergetter.getOrThrow(NetherFeatures.CRIMSON_FOREST_VEGETATION);
        Holder<WorldGenFeatureConfigured<?, ?>> holder7 = holdergetter.getOrThrow(NetherFeatures.WARPED_FOREST_VEGETION);
        Holder<WorldGenFeatureConfigured<?, ?>> holder8 = holdergetter.getOrThrow(NetherFeatures.NETHER_SPROUTS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder9 = holdergetter.getOrThrow(NetherFeatures.TWISTING_VINES);
        Holder<WorldGenFeatureConfigured<?, ?>> holder10 = holdergetter.getOrThrow(NetherFeatures.WEEPING_VINES);
        Holder<WorldGenFeatureConfigured<?, ?>> holder11 = holdergetter.getOrThrow(NetherFeatures.PATCH_CRIMSON_ROOTS);
        Holder<WorldGenFeatureConfigured<?, ?>> holder12 = holdergetter.getOrThrow(NetherFeatures.BASALT_PILLAR);
        Holder<WorldGenFeatureConfigured<?, ?>> holder13 = holdergetter.getOrThrow(NetherFeatures.SPRING_LAVA_NETHER);
        Holder<WorldGenFeatureConfigured<?, ?>> holder14 = holdergetter.getOrThrow(NetherFeatures.SPRING_NETHER_CLOSED);
        Holder<WorldGenFeatureConfigured<?, ?>> holder15 = holdergetter.getOrThrow(NetherFeatures.SPRING_NETHER_OPEN);
        Holder<WorldGenFeatureConfigured<?, ?>> holder16 = holdergetter.getOrThrow(NetherFeatures.PATCH_SOUL_FIRE);
        Holder<WorldGenFeatureConfigured<?, ?>> holder17 = holdergetter.getOrThrow(NetherFeatures.PATCH_FIRE);

        PlacementUtils.register(bootstapcontext, NetherPlacements.DELTA, holder, CountOnEveryLayerPlacement.of(40), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.SMALL_BASALT_COLUMNS, holder1, CountOnEveryLayerPlacement.of(4), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.LARGE_BASALT_COLUMNS, holder2, CountOnEveryLayerPlacement.of(2), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.BASALT_BLOBS, holder3, CountPlacement.of(75), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.BLACKSTONE_BLOBS, holder4, CountPlacement.of(25), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.GLOWSTONE_EXTRA, holder5, CountPlacement.of(BiasedToBottomInt.of(0, 9)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.GLOWSTONE, holder5, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.CRIMSON_FOREST_VEGETATION, holder6, CountOnEveryLayerPlacement.of(6), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.WARPED_FOREST_VEGETATION, holder7, CountOnEveryLayerPlacement.of(5), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.NETHER_SPROUTS, holder8, CountOnEveryLayerPlacement.of(4), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.TWISTING_VINES, holder9, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.WEEPING_VINES, holder10, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.PATCH_CRIMSON_ROOTS, holder11, PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.BASALT_PILLAR, holder12, CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.SPRING_DELTA, holder13, CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.SPRING_CLOSED, holder14, CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.SPRING_CLOSED_DOUBLE, holder14, CountPlacement.of(32), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, NetherPlacements.SPRING_OPEN, holder15, CountPlacement.of(8), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());
        List<PlacementModifier> list = List.of(CountPlacement.of(UniformInt.of(0, 5)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome());

        PlacementUtils.register(bootstapcontext, NetherPlacements.PATCH_SOUL_FIRE, holder16, list);
        PlacementUtils.register(bootstapcontext, NetherPlacements.PATCH_FIRE, holder17, list);
    }
}
