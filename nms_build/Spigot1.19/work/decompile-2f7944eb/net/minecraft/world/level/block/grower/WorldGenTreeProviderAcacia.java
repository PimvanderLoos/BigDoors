package net.minecraft.world.level.block.grower;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderAcacia extends WorldGenTreeProvider {

    public WorldGenTreeProviderAcacia() {}

    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(RandomSource randomsource, boolean flag) {
        return TreeFeatures.ACACIA;
    }
}
