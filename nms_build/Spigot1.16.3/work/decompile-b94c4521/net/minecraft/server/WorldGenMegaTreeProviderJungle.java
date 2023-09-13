package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenMegaTreeProviderJungle extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderJungle() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return BiomeDecoratorGroups.JUNGLE_TREE_NO_VINE;
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random) {
        return BiomeDecoratorGroups.MEGA_JUNGLE_TREE;
    }
}
