package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderSpruce extends WorldGenMegaTreeProvider {

    public WorldGenTreeProviderSpruce() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return WorldGenerator.TREE.b((WorldGenFeatureConfiguration) BiomeDecoratorGroups.SPRUCE_TREE);
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random) {
        return WorldGenerator.TREE.b((WorldGenFeatureConfiguration) (random.nextBoolean() ? BiomeDecoratorGroups.MEGA_SPRUCE_TREE : BiomeDecoratorGroups.MEGA_PINE_TREE));
    }
}
