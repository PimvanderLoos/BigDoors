package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenMegaTreeProviderDarkOak extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderDarkOak() {}

    @Nullable
    @Override
    protected ResourceKey<WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(RandomSource randomsource, boolean flag) {
        return null;
    }

    @Nullable
    @Override
    protected ResourceKey<WorldGenFeatureConfigured<?, ?>> getConfiguredMegaFeature(RandomSource randomsource) {
        return TreeFeatures.DARK_OAK;
    }
}
