package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureUtils {

    public FeatureUtils() {}

    public static Holder<? extends WorldGenFeatureConfigured<?, ?>> bootstrap(IRegistry<WorldGenFeatureConfigured<?, ?>> iregistry) {
        List<Holder<? extends WorldGenFeatureConfigured<?, ?>>> list = List.of(AquaticFeatures.KELP, CaveFeatures.MOSS_PATCH_BONEMEAL, EndFeatures.CHORUS_PLANT, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, NetherFeatures.BASALT_BLOBS, OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, PileFeatures.PILE_HAY, TreeFeatures.AZALEA_TREE, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);

        return (Holder) SystemUtils.getRandom(list, RandomSource.create());
    }

    private static BlockPredicate simplePatchPredicate(List<Block> list) {
        BlockPredicate blockpredicate;

        if (!list.isEmpty()) {
            blockpredicate = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(EnumDirection.DOWN.getNormal(), list));
        } else {
            blockpredicate = BlockPredicate.ONLY_IN_AIR_PREDICATE;
        }

        return blockpredicate;
    }

    public static WorldGenFeatureRandomPatchConfiguration simpleRandomPatchConfiguration(int i, Holder<PlacedFeature> holder) {
        return new WorldGenFeatureRandomPatchConfiguration(i, 7, 3, holder);
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> WorldGenFeatureRandomPatchConfiguration simplePatchConfiguration(F f0, FC fc, List<Block> list, int i) {
        return simpleRandomPatchConfiguration(i, PlacementUtils.filtered(f0, fc, simplePatchPredicate(list)));
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> WorldGenFeatureRandomPatchConfiguration simplePatchConfiguration(F f0, FC fc, List<Block> list) {
        return simplePatchConfiguration(f0, fc, list, 96);
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> WorldGenFeatureRandomPatchConfiguration simplePatchConfiguration(F f0, FC fc) {
        return simplePatchConfiguration(f0, fc, List.of(), 96);
    }

    public static Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> register(String s, WorldGenerator<WorldGenFeatureEmptyConfiguration> worldgenerator) {
        return register(s, worldgenerator, WorldGenFeatureConfiguration.NONE);
    }

    public static <FC extends WorldGenFeatureConfiguration, F extends WorldGenerator<FC>> Holder<WorldGenFeatureConfigured<FC, ?>> register(String s, F f0, FC fc) {
        return RegistryGeneration.registerExact(RegistryGeneration.CONFIGURED_FEATURE, s, new WorldGenFeatureConfigured<>(f0, fc));
    }
}
