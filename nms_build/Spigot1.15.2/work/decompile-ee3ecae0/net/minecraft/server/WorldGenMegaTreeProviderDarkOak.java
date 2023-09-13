package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenMegaTreeProviderDarkOak extends WorldGenMegaTreeProvider {

    public WorldGenMegaTreeProviderDarkOak() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureSmallTreeConfigurationConfiguration, ?> a(Random random, boolean flag) {
        return null;
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenMegaTreeConfiguration, ?> a(Random random) {
        return WorldGenerator.DARK_OAK_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.DARK_OAK_TREE);
    }
}
