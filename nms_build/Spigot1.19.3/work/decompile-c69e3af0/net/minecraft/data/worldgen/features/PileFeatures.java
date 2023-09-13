package net.minecraft.data.worldgen.features;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderRotatedBlock;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;

public class PileFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PILE_HAY = FeatureUtils.createKey("pile_hay");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PILE_MELON = FeatureUtils.createKey("pile_melon");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PILE_SNOW = FeatureUtils.createKey("pile_snow");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PILE_ICE = FeatureUtils.createKey("pile_ice");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> PILE_PUMPKIN = FeatureUtils.createKey("pile_pumpkin");

    public PileFeatures() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        FeatureUtils.register(bootstapcontext, PileFeatures.PILE_HAY, WorldGenerator.BLOCK_PILE, new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderRotatedBlock(Blocks.HAY_BLOCK)));
        FeatureUtils.register(bootstapcontext, PileFeatures.PILE_MELON, WorldGenerator.BLOCK_PILE, new WorldGenFeatureBlockPileConfiguration(WorldGenFeatureStateProvider.simple(Blocks.MELON)));
        FeatureUtils.register(bootstapcontext, PileFeatures.PILE_SNOW, WorldGenerator.BLOCK_PILE, new WorldGenFeatureBlockPileConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SNOW)));
        FeatureUtils.register(bootstapcontext, PileFeatures.PILE_ICE, WorldGenerator.BLOCK_PILE, new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.BLUE_ICE.defaultBlockState(), 1).add(Blocks.PACKED_ICE.defaultBlockState(), 5))));
        FeatureUtils.register(bootstapcontext, PileFeatures.PILE_PUMPKIN, WorldGenerator.BLOCK_PILE, new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.PUMPKIN.defaultBlockState(), 19).add(Blocks.JACK_O_LANTERN.defaultBlockState(), 1))));
    }
}
