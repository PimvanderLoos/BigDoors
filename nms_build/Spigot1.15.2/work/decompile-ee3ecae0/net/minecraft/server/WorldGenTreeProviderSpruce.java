package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderSpruce extends WorldGenMegaTreeProvider {

    public WorldGenTreeProviderSpruce() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureSmallTreeConfigurationConfiguration, ?> a(Random random, boolean flag) {
        return WorldGenerator.NORMAL_TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE);
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenMegaTreeConfiguration, ?> a(Random random) {
        return WorldGenerator.MEGA_SPRUCE_TREE.b((WorldGenFeatureConfiguration) (random.nextBoolean() ? BiomeDecoratorGroups.MEGA_SPRUCE_TREE : BiomeDecoratorGroups.MEGA_PINE_TREE));
    }
}
