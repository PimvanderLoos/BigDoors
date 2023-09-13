package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderSpruce extends WorldGenMegaTreeProvider {

    public WorldGenTreeProviderSpruce() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return BiomeDecoratorGroups.SPRUCE;
    }

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random) {
        return random.nextBoolean() ? BiomeDecoratorGroups.MEGA_SPRUCE : BiomeDecoratorGroups.MEGA_PINE;
    }
}
