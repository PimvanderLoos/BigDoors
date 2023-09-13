package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenFeatureStateProviders<P extends WorldGenFeatureStateProvider> {

    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderSimpl> SIMPLE_STATE_PROVIDER = a("simple_state_provider", WorldGenFeatureStateProviderSimpl.CODEC);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderWeighted> WEIGHTED_STATE_PROVIDER = a("weighted_state_provider", WorldGenFeatureStateProviderWeighted.CODEC);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderPlainFlower> PLAIN_FLOWER_PROVIDER = a("plain_flower_provider", WorldGenFeatureStateProviderPlainFlower.CODEC);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderForestFlower> FOREST_FLOWER_PROVIDER = a("forest_flower_provider", WorldGenFeatureStateProviderForestFlower.CODEC);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderRotatedBlock> ROTATED_BLOCK_PROVIDER = a("rotated_block_provider", WorldGenFeatureStateProviderRotatedBlock.CODEC);
    public static final WorldGenFeatureStateProviders<RandomizedIntStateProvider> RANDOMIZED_INT_STATE_PROVIDER = a("randomized_int_state_provider", RandomizedIntStateProvider.CODEC);
    private final Codec<P> codec;

    private static <P extends WorldGenFeatureStateProvider> WorldGenFeatureStateProviders<P> a(String s, Codec<P> codec) {
        return (WorldGenFeatureStateProviders) IRegistry.a(IRegistry.BLOCKSTATE_PROVIDER_TYPES, s, (Object) (new WorldGenFeatureStateProviders<>(codec)));
    }

    private WorldGenFeatureStateProviders(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> a() {
        return this.codec;
    }
}
