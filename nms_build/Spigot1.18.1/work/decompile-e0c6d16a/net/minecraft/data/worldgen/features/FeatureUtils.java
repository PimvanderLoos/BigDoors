package net.minecraft.data.worldgen.features;

import java.util.List;
import java.util.Random;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureUtils {

    public FeatureUtils() {}

    public static WorldGenFeatureConfigured<?, ?> bootstrap() {
        WorldGenFeatureConfigured<?, ?>[] aworldgenfeatureconfigured = new WorldGenFeatureConfigured[]{AquaticFeatures.KELP, CaveFeatures.MOSS_PATCH_BONEMEAL, EndFeatures.CHORUS_PLANT, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, NetherFeatures.BASALT_BLOBS, OreFeatures.ORE_ANCIENT_DEBRIS_LARGE, PileFeatures.PILE_HAY, TreeFeatures.AZALEA_TREE, VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA};

        return (WorldGenFeatureConfigured) SystemUtils.getRandom((Object[]) aworldgenfeatureconfigured, new Random());
    }

    private static BlockPredicate simplePatchPredicate(List<Block> list) {
        BlockPredicate blockpredicate;

        if (!list.isEmpty()) {
            blockpredicate = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesBlocks(list, new BlockPosition(0, -1, 0)));
        } else {
            blockpredicate = BlockPredicate.ONLY_IN_AIR_PREDICATE;
        }

        return blockpredicate;
    }

    public static WorldGenFeatureRandomPatchConfiguration simpleRandomPatchConfiguration(int i, PlacedFeature placedfeature) {
        return new WorldGenFeatureRandomPatchConfiguration(i, 7, 3, () -> {
            return placedfeature;
        });
    }

    public static WorldGenFeatureRandomPatchConfiguration simplePatchConfiguration(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, List<Block> list, int i) {
        return simpleRandomPatchConfiguration(i, worldgenfeatureconfigured.filtered(simplePatchPredicate(list)));
    }

    public static WorldGenFeatureRandomPatchConfiguration simplePatchConfiguration(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, List<Block> list) {
        return simplePatchConfiguration(worldgenfeatureconfigured, list, 96);
    }

    public static WorldGenFeatureRandomPatchConfiguration simplePatchConfiguration(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        return simplePatchConfiguration(worldgenfeatureconfigured, List.of(), 96);
    }

    public static <FC extends WorldGenFeatureConfiguration> WorldGenFeatureConfigured<FC, ?> register(String s, WorldGenFeatureConfigured<FC, ?> worldgenfeatureconfigured) {
        return (WorldGenFeatureConfigured) IRegistry.register(RegistryGeneration.CONFIGURED_FEATURE, s, worldgenfeatureconfigured);
    }
}
