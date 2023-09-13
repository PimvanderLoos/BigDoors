package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CarvingMaskPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class AquaticPlacements {

    public static final ResourceKey<PlacedFeature> SEAGRASS_WARM = PlacementUtils.createKey("seagrass_warm");
    public static final ResourceKey<PlacedFeature> SEAGRASS_NORMAL = PlacementUtils.createKey("seagrass_normal");
    public static final ResourceKey<PlacedFeature> SEAGRASS_COLD = PlacementUtils.createKey("seagrass_cold");
    public static final ResourceKey<PlacedFeature> SEAGRASS_RIVER = PlacementUtils.createKey("seagrass_river");
    public static final ResourceKey<PlacedFeature> SEAGRASS_SWAMP = PlacementUtils.createKey("seagrass_swamp");
    public static final ResourceKey<PlacedFeature> SEAGRASS_DEEP_WARM = PlacementUtils.createKey("seagrass_deep_warm");
    public static final ResourceKey<PlacedFeature> SEAGRASS_DEEP = PlacementUtils.createKey("seagrass_deep");
    public static final ResourceKey<PlacedFeature> SEAGRASS_DEEP_COLD = PlacementUtils.createKey("seagrass_deep_cold");
    public static final ResourceKey<PlacedFeature> SEAGRASS_SIMPLE = PlacementUtils.createKey("seagrass_simple");
    public static final ResourceKey<PlacedFeature> SEA_PICKLE = PlacementUtils.createKey("sea_pickle");
    public static final ResourceKey<PlacedFeature> KELP_COLD = PlacementUtils.createKey("kelp_cold");
    public static final ResourceKey<PlacedFeature> KELP_WARM = PlacementUtils.createKey("kelp_warm");
    public static final ResourceKey<PlacedFeature> WARM_OCEAN_VEGETATION = PlacementUtils.createKey("warm_ocean_vegetation");

    public AquaticPlacements() {}

    private static List<PlacementModifier> seagrassPlacement(int i) {
        return List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, CountPlacement.of(i), BiomeFilter.biome());
    }

    public static void bootstrap(BootstapContext<PlacedFeature> bootstapcontext) {
        HolderGetter<WorldGenFeatureConfigured<?, ?>> holdergetter = bootstapcontext.lookup(Registries.CONFIGURED_FEATURE);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c = holdergetter.getOrThrow(AquaticFeatures.SEAGRASS_SHORT);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c1 = holdergetter.getOrThrow(AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c2 = holdergetter.getOrThrow(AquaticFeatures.SEAGRASS_MID);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c3 = holdergetter.getOrThrow(AquaticFeatures.SEAGRASS_TALL);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c4 = holdergetter.getOrThrow(AquaticFeatures.SEAGRASS_SIMPLE);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c5 = holdergetter.getOrThrow(AquaticFeatures.SEA_PICKLE);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c6 = holdergetter.getOrThrow(AquaticFeatures.KELP);
        Holder.c<WorldGenFeatureConfigured<?, ?>> holder_c7 = holdergetter.getOrThrow(AquaticFeatures.WARM_OCEAN_VEGETATION);

        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_WARM, holder_c, seagrassPlacement(80));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_NORMAL, holder_c, seagrassPlacement(48));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_COLD, holder_c, seagrassPlacement(32));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_RIVER, holder_c1, seagrassPlacement(48));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_SWAMP, holder_c2, seagrassPlacement(64));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_DEEP_WARM, holder_c3, seagrassPlacement(80));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_DEEP, holder_c3, seagrassPlacement(48));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_DEEP_COLD, holder_c3, seagrassPlacement(40));
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEAGRASS_SIMPLE, holder_c4, CarvingMaskPlacement.forStep(WorldGenStage.Features.LIQUID), RarityFilter.onAverageOnceEvery(10), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), Blocks.STONE), BlockPredicate.matchesBlocks(BlockPosition.ZERO, Blocks.WATER), BlockPredicate.matchesBlocks(EnumDirection.UP.getNormal(), Blocks.WATER))), BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, AquaticPlacements.SEA_PICKLE, holder_c5, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, AquaticPlacements.KELP_COLD, holder_c6, NoiseBasedCountPlacement.of(120, 80.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, AquaticPlacements.KELP_WARM, holder_c6, NoiseBasedCountPlacement.of(80, 80.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
        PlacementUtils.register(bootstapcontext, AquaticPlacements.WARM_OCEAN_VEGETATION, holder_c7, NoiseBasedCountPlacement.of(20, 400.0D, 0.0D), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
    }
}
