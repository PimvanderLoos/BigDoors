package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderOak extends WorldGenTreeProvider {

    public WorldGenTreeProviderOak() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return random.nextInt(10) == 0 ? WorldGenerator.TREE.b((WorldGenFeatureConfiguration) (flag ? BiomeDecoratorGroups.FANCY_TREE_BEES_005 : BiomeDecoratorGroups.FANCY_TREE)) : WorldGenerator.TREE.b((WorldGenFeatureConfiguration) (flag ? BiomeDecoratorGroups.NORMAL_TREE_BEES_005 : BiomeDecoratorGroups.NORMAL_TREE));
    }
}
