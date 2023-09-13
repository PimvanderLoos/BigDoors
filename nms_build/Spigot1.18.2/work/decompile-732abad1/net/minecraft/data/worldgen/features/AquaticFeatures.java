package net.minecraft.data.worldgen.features;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.placement.PlacementUtils;
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

    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureConfigurationChance, ?>> SEAGRASS_SHORT = FeatureUtils.register("seagrass_short", WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.3F));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureConfigurationChance, ?>> SEAGRASS_SLIGHTLY_LESS_SHORT = FeatureUtils.register("seagrass_slightly_less_short", WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.4F));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureConfigurationChance, ?>> SEAGRASS_MID = FeatureUtils.register("seagrass_mid", WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.6F));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureConfigurationChance, ?>> SEAGRASS_TALL = FeatureUtils.register("seagrass_tall", WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.8F));
    public static final Holder<WorldGenFeatureConfigured<WorldGenDecoratorFrequencyConfiguration, ?>> SEA_PICKLE = FeatureUtils.register("sea_pickle", WorldGenerator.SEA_PICKLE, new WorldGenDecoratorFrequencyConfiguration(20));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureBlockConfiguration, ?>> SEAGRASS_SIMPLE = FeatureUtils.register("seagrass_simple", WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SEAGRASS)));
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> KELP = FeatureUtils.register("kelp", WorldGenerator.KELP);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureRandom2, ?>> WARM_OCEAN_VEGETATION = FeatureUtils.register("warm_ocean_vegetation", WorldGenerator.SIMPLE_RANDOM_SELECTOR, new WorldGenFeatureRandom2(HolderSet.direct(PlacementUtils.inlinePlaced(WorldGenerator.CORAL_TREE, WorldGenFeatureConfiguration.NONE), PlacementUtils.inlinePlaced(WorldGenerator.CORAL_CLAW, WorldGenFeatureConfiguration.NONE), PlacementUtils.inlinePlaced(WorldGenerator.CORAL_MUSHROOM, WorldGenFeatureConfiguration.NONE))));

    public AquaticFeatures() {}
}
