package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenMegaTreeProviderJungle extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderJungle() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return (new WorldGenTrees(WorldGenFeatureTreeConfiguration.a)).b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.JUNGLE_TREE_NOVINE);
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random) {
        return WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.MEGA_JUNGLE_TREE);
    }
}
