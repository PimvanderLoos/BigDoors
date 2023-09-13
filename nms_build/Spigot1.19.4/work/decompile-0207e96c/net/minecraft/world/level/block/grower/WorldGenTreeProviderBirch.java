package net.minecraft.world.level.block.grower;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderBirch extends WorldGenTreeProvider {

    public WorldGenTreeProviderBirch() {}

    @Override
    protected ResourceKey<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(RandomSource randomsource, boolean flag) {
        return flag ? TreeFeatures.BIRCH_BEES_005 : TreeFeatures.BIRCH;
    }
}
