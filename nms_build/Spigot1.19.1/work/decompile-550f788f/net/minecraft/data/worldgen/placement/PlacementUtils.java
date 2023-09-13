package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class PlacementUtils {

    public static final PlacementModifier HEIGHTMAP = HeightmapPlacement.onHeightmap(HeightMap.Type.MOTION_BLOCKING);
    public static final PlacementModifier HEIGHTMAP_TOP_SOLID = HeightmapPlacement.onHeightmap(HeightMap.Type.OCEAN_FLOOR_WG);
    public static final PlacementModifier HEIGHTMAP_WORLD_SURFACE = HeightmapPlacement.onHeightmap(HeightMap.Type.WORLD_SURFACE_WG);
    public static final PlacementModifier HEIGHTMAP_OCEAN_FLOOR = HeightmapPlacement.onHeightmap(HeightMap.Type.OCEAN_FLOOR);
    public static final PlacementModifier FULL_RANGE = HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top());
    public static final PlacementModifier RANGE_10_10 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(10), VerticalAnchor.belowTop(10));
    public static final PlacementModifier RANGE_8_8 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(8), VerticalAnchor.belowTop(8));
    public static final PlacementModifier RANGE_4_4 = HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(4), VerticalAnchor.belowTop(4));
    public static final PlacementModifier RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT = HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(256));

    public PlacementUtils() {}

    public static Holder<PlacedFeature> bootstrap(IRegistry<PlacedFeature> iregistry) {
        List<Holder<PlacedFeature>> list = List.of(AquaticPlacements.KELP_COLD, CavePlacements.CAVE_VINES, EndPlacements.CHORUS_PLANT, MiscOverworldPlacements.BLUE_ICE, NetherPlacements.BASALT_BLOBS, OrePlacements.ORE_ANCIENT_DEBRIS_LARGE, TreePlacements.ACACIA_CHECKED, VegetationPlacements.BAMBOO_VEGETATION, VillagePlacements.PILE_HAY_VILLAGE);

        return (Holder) SystemUtils.getRandom(list, RandomSource.create());
    }

    public static Holder<PlacedFeature> register(String s, Holder<? extends WorldGenFeatureConfigured<?, ?>> holder, List<PlacementModifier> list) {
        return RegistryGeneration.register(RegistryGeneration.PLACED_FEATURE, s, new PlacedFeature(Holder.hackyErase(holder), List.copyOf(list)));
    }

    public static Holder<PlacedFeature> register(String s, Holder<? extends WorldGenFeatureConfigured<?, ?>> holder, PlacementModifier... aplacementmodifier) {
        return register(s, holder, List.of(aplacementmodifier));
    }

    public static PlacementModifier countExtra(int i, float f, int j) {
        float f1 = 1.0F / f;

        if (Math.abs(f1 - (float) ((int) f1)) > 1.0E-5F) {
            throw new IllegalStateException("Chance data cannot be represented as list weight");
        } else {
            SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist = SimpleWeightedRandomList.builder().add(ConstantInt.of(i), (int) f1 - 1).add(ConstantInt.of(i + j), 1).build();

            return CountPlacement.of(new WeightedListInt(simpleweightedrandomlist));
        }
    }

    public static PlacementFilter isEmpty() {
        return BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE);
    }

    public static BlockPredicateFilter filteredByBlockSurvival(Block block) {
        return BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(block.defaultBlockState(), BlockPosition.ZERO));
    }

    public static Holder<PlacedFeature> inlinePlaced(Holder<? extends WorldGenFeatureConfigured<?, ?>> holder, PlacementModifier... aplacementmodifier) {
        return Holder.direct(new PlacedFeature(Holder.hackyErase(holder), List.of(aplacementmodifier)));
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> Holder<PlacedFeature> inlinePlaced(F f0, FC fc, PlacementModifier... aplacementmodifier) {
        return inlinePlaced(Holder.direct(new WorldGenFeatureConfigured<>(f0, fc)), aplacementmodifier);
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> Holder<PlacedFeature> onlyWhenEmpty(F f0, FC fc) {
        return filtered(f0, fc, BlockPredicate.ONLY_IN_AIR_PREDICATE);
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> Holder<PlacedFeature> filtered(F f0, FC fc, BlockPredicate blockpredicate) {
        return inlinePlaced(f0, fc, BlockPredicateFilter.forPredicate(blockpredicate));
    }
}
