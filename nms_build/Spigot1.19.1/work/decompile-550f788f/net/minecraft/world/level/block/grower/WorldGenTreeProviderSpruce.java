package net.minecraft.world.level.block.grower;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderSpruce extends WorldGenMegaTreeProvider {

    public WorldGenTreeProviderSpruce() {}

    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(RandomSource randomsource, boolean flag) {
        return TreeFeatures.SPRUCE;
    }

    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredMegaFeature(RandomSource randomsource) {
        return randomsource.nextBoolean() ? TreeFeatures.MEGA_SPRUCE : TreeFeatures.MEGA_PINE;
    }
}
