package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenMegaTreeProviderDarkOak extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderDarkOak() {}

    @Nullable
    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(Random random, boolean flag) {
        return null;
    }

    @Nullable
    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredMegaFeature(Random random) {
        return TreeFeatures.DARK_OAK;
    }
}
