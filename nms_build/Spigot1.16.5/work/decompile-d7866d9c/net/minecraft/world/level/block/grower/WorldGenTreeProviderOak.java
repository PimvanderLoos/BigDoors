package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;

public class WorldGenTreeProviderOak extends WorldGenTreeProvider {

    public WorldGenTreeProviderOak() {}

    @Nullable
    @Override
    protected WorldGenFeatureConfigured<WorldGenFeatureTreeConfiguration, ?> a(Random random, boolean flag) {
        return random.nextInt(10) == 0 ? (flag ? BiomeDecoratorGroups.FANCY_OAK_BEES_005 : BiomeDecoratorGroups.FANCY_OAK) : (flag ? BiomeDecoratorGroups.OAK_BEES_005 : BiomeDecoratorGroups.OAK);
    }
}
