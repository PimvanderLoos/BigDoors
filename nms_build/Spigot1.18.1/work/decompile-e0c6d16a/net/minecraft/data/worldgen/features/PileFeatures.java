package net.minecraft.data.worldgen.features;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderRotatedBlock;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviderWeighted;

public class PileFeatures {

    public static final WorldGenFeatureConfigured<?, ?> PILE_HAY = FeatureUtils.register("pile_hay", WorldGenerator.BLOCK_PILE.configured(new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderRotatedBlock(Blocks.HAY_BLOCK))));
    public static final WorldGenFeatureConfigured<?, ?> PILE_MELON = FeatureUtils.register("pile_melon", WorldGenerator.BLOCK_PILE.configured(new WorldGenFeatureBlockPileConfiguration(WorldGenFeatureStateProvider.simple(Blocks.MELON))));
    public static final WorldGenFeatureConfigured<?, ?> PILE_SNOW = FeatureUtils.register("pile_snow", WorldGenerator.BLOCK_PILE.configured(new WorldGenFeatureBlockPileConfiguration(WorldGenFeatureStateProvider.simple(Blocks.SNOW))));
    public static final WorldGenFeatureConfigured<?, ?> PILE_ICE = FeatureUtils.register("pile_ice", WorldGenerator.BLOCK_PILE.configured(new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.BLUE_ICE.defaultBlockState(), 1).add(Blocks.PACKED_ICE.defaultBlockState(), 5)))));
    public static final WorldGenFeatureConfigured<?, ?> PILE_PUMPKIN = FeatureUtils.register("pile_pumpkin", WorldGenerator.BLOCK_PILE.configured(new WorldGenFeatureBlockPileConfiguration(new WorldGenFeatureStateProviderWeighted(SimpleWeightedRandomList.builder().add(Blocks.PUMPKIN.defaultBlockState(), 19).add(Blocks.JACK_O_LANTERN.defaultBlockState(), 1)))));

    public PileFeatures() {}
}
