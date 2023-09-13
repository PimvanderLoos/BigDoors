package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenMegaTreeProviderJungle extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderJungle() {}

    @Override
    protected WorldGenFeatureConfigured<?, ?> getConfiguredFeature(Random random, boolean flag) {
        return TreeFeatures.JUNGLE_TREE_NO_VINE;
    }

    @Override
    protected WorldGenFeatureConfigured<?, ?> getConfiguredMegaFeature(Random random) {
        return TreeFeatures.MEGA_JUNGLE_TREE;
    }
}
