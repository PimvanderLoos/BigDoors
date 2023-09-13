package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenTreeProviderOak extends WorldGenTreeProvider {

    public WorldGenTreeProviderOak() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return random.nextInt(10) == 0 ? (flag ? BiomeDecoratorGroups.FANCY_OAK_BEES_005 : BiomeDecoratorGroups.FANCY_OAK) : (flag ? BiomeDecoratorGroups.OAK_BEES_005 : BiomeDecoratorGroups.OAK);
    }
}
