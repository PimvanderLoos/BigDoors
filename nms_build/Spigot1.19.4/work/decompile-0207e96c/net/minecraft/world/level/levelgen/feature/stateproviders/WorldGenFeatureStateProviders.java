package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class WorldGenFeatureStateProviders<P extends WorldGenFeatureStateProvider> {

    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderSimpl> SIMPLE_STATE_PROVIDER = register("simple_state_provider", WorldGenFeatureStateProviderSimpl.CODEC);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderWeighted> WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WorldGenFeatureStateProviderWeighted.CODEC);
    public static final WorldGenFeatureStateProviders<NoiseThresholdProvider> NOISE_THRESHOLD_PROVIDER = register("noise_threshold_provider", NoiseThresholdProvider.CODEC);
    public static final WorldGenFeatureStateProviders<NoiseProvider> NOISE_PROVIDER = register("noise_provider", NoiseProvider.CODEC);
    public static final WorldGenFeatureStateProviders<DualNoiseProvider> DUAL_NOISE_PROVIDER = register("dual_noise_provider", DualNoiseProvider.CODEC);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderRotatedBlock> ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", WorldGenFeatureStateProviderRotatedBlock.CODEC);
    public static final WorldGenFeatureStateProviders<RandomizedIntStateProvider> RANDOMIZED_INT_STATE_PROVIDER = register("randomized_int_state_provider", RandomizedIntStateProvider.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenFeatureStateProvider> WorldGenFeatureStateProviders<P> register(String s, Codec<P> codec) {
        return (WorldGenFeatureStateProviders) IRegistry.register(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE, s, new WorldGenFeatureStateProviders<>(codec));
    }

    private WorldGenFeatureStateProviders(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> codec() {
        return this.codec;
    }
}
