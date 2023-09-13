package net.minecraft.data.worldgen.features;

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureRandom2;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class AquaticFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SEAGRASS_SHORT = FeatureUtils.createKey("seagrass_short");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SEAGRASS_SLIGHTLY_LESS_SHORT = FeatureUtils.createKey("seagrass_slightly_less_short");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SEAGRASS_MID = FeatureUtils.createKey("seagrass_mid");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SEAGRASS_TALL = FeatureUtils.createKey("seagrass_tall");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SEA_PICKLE = FeatureUtils.createKey("sea_pickle");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> SEAGRASS_SIMPLE = FeatureUtils.createKey("seagrass_simple");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> KELP = FeatureUtils.createKey("kelp");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> WARM_OCEAN_VEGETATION = FeatureUtils.createKey("warm_ocean_vegetation");

    public AquaticFeatures() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        FeatureUtils.register(bootstapcontext, AquaticFeatures.SEAGRASS_SHORT, WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.3F));
        FeatureUtils.register(bootstapcontext, AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT, WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.4F));
        FeatureUtils.register(bootstapcontext, AquaticFeatures.SEAGRASS_MID, WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.6F));
        FeatureUtils.register(bootstapcontext, AquaticFeatures.SEAGRASS_TALL, WorldGenerator.SEAGRASS, new WorldGenFeatureConfigurationChance(0.8F));
        FeatureUtils.register(bootstapcontext, AquaticFeatures.SEA_PICKLE, WorldGenerator.SEA_PICKLE, new WorldGenDecoratorFrequencyConfiguration(20));
        FeatureUtils.register(bootstapcontext, AquaticFeatures.SEAGRASS_SIMPLE, WorldGenerator.SIMPLE_BLOCK, new WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SEAGRASS)));
        FeatureUtils.register(bootstapcontext, AquaticFeatures.KELP, WorldGenerator.KELP);
        FeatureUtils.register(bootstapcontext, AquaticFeatures.WARM_OCEAN_VEGETATION, WorldGenerator.SIMPLE_RANDOM_SELECTOR, new WorldGenFeatureRandom2(HolderSet.direct(PlacementUtils.inlinePlaced(WorldGenerator.CORAL_TREE, WorldGenFeatureConfiguration.NONE), PlacementUtils.inlinePlaced(WorldGenerator.CORAL_CLAW, WorldGenFeatureConfiguration.NONE), PlacementUtils.inlinePlaced(WorldGenerator.CORAL_MUSHROOM, WorldGenFeatureConfiguration.NONE))));
    }
}
