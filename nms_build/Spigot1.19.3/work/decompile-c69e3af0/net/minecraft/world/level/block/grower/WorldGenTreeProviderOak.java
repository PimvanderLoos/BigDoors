package net.minecraft.world.level.block.grower;

import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderOak extends WorldGenTreeProvider {

    public WorldGenTreeProviderOak() {}

    @Override
    protected ResourceKey<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(RandomSource randomsource, boolean flag) {
        return randomsource.nextInt(10) == 0 ? (flag ? TreeFeatures.FANCY_OAK_BEES_005 : TreeFeatures.FANCY_OAK) : (flag ? TreeFeatures.OAK_BEES_005 : TreeFeatures.OAK);
    }
}
