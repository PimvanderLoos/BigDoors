package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderSpruce extends WorldGenMegaTreeProvider {

    public WorldGenTreeProviderSpruce() {}

    @Override
    protected WorldGenFeatureConfigured<?, ?> getConfiguredFeature(Random random, boolean flag) {
        return TreeFeatures.SPRUCE;
    }

    @Override
    protected WorldGenFeatureConfigured<?, ?> getConfiguredMegaFeature(Random random) {
        return random.nextBoolean() ? TreeFeatures.MEGA_SPRUCE : TreeFeatures.MEGA_PINE;
    }
}
