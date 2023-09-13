package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderBirch extends WorldGenTreeProvider {

    public WorldGenTreeProviderBirch() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureSmallTreeConfigurationConfiguration, ?> a(Random random, boolean flag) {
        return WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) (flag ? BiomeDecoratorGroups.BIRCH_TREE_BEES_005 : BiomeDecoratorGroups.BIRCH_TREE));
    }
}
