package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class AquaticFeatures {

    public static final WorldGenFeatureConfigured<?, ?> SEAGRASS_SHORT = FeatureUtils.register("seagrass_short", WorldGenerator.SEAGRASS.configured(new WorldGenFeatureConfigurationChance(0.3F)));
    public static final WorldGenFeatureConfigured<?, ?> SEAGRASS_SLIGHTLY_LESS_SHORT = FeatureUtils.register("seagrass_slightly_less_short", WorldGenerator.SEAGRASS.configured(new WorldGenFeatureConfigurationChance(0.4F)));
    public static final WorldGenFeatureConfigured<?, ?> SEAGRASS_MID = FeatureUtils.register("seagrass_mid", WorldGenerator.SEAGRASS.configured(new WorldGenFeatureConfigurationChance(0.6F)));
    public static final WorldGenFeatureConfigured<?, ?> SEAGRASS_TALL = FeatureUtils.register("seagrass_tall", WorldGenerator.SEAGRASS.configured(new WorldGenFeatureConfigurationChance(0.8F)));
    public static final WorldGenFeatureConfigured<?, ?> SEA_PICKLE = FeatureUtils.register("sea_pickle", WorldGenerator.SEA_PICKLE.configured(new WorldGenDecoratorFrequencyConfiguration(20)));
    public static final WorldGenFeatureConfigured<?, ?> SEAGRASS_SIMPLE = FeatureUtils.register("seagrass_simple", WorldGenerator.SIMPLE_BLOCK.configured(new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SEAGRASS))));
    public static final WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?> KELP = FeatureUtils.register("kelp", WorldGenerator.KELP.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<WorldGenFeatureRandom2, ?> WARM_OCEAN_VEGETATION = FeatureUtils.register("warm_ocean_vegetation", WorldGenerator.SIMPLE_RANDOM_SELECTOR.configured(new WorldGenFeatureRandom2(List.of(() -> {
        return WorldGenerator.CORAL_TREE.configured(WorldGenFeatureConfiguration.NONE).placed();
    }, () -> {
        return WorldGenerator.CORAL_CLAW.configured(WorldGenFeatureConfiguration.NONE).placed();
    }, () -> {
        return WorldGenerator.CORAL_MUSHROOM.configured(WorldGenFeatureConfiguration.NONE).placed();
    }))));

    public AquaticFeatures() {}
}
