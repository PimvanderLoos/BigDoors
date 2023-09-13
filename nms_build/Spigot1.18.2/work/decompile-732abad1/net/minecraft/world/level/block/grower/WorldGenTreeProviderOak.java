package net.minecraft.world.level.block.grower;

import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenTreeProviderOak extends WorldGenTreeProvider {

    public WorldGenTreeProviderOak() {}

    @Override
    protected Holder<? extends WorldGenFeatureConfigured<?, ?>> getConfiguredFeature(Random random, boolean flag) {
        return random.nextInt(10) == 0 ? (flag ? TreeFeatures.FANCY_OAK_BEES_005 : TreeFeatures.FANCY_OAK) : (flag ? TreeFeatures.OAK_BEES_005 : TreeFeatures.OAK);
    }
}
