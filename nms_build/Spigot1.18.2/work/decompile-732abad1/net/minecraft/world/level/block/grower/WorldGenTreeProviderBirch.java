package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderBirch extends WorldGenTreeProvider {

    public WorldGenTreeProviderBirch() {}

    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(Random random, boolean flag) {
        return flag ? TreeFeatures.BIRCH_BEES_005 : TreeFeatures.BIRCH;
    }
}
