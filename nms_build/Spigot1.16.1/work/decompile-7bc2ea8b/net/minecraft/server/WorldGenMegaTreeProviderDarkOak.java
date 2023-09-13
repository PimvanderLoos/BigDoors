package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenMegaTreeProviderDarkOak extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderDarkOak() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return null;
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random) {
        return WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.DARK_OAK_TREE);
    }
}
