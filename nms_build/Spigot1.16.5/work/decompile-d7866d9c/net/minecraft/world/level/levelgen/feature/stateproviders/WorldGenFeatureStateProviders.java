package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class WorldGenFeatureStateProviders<P extends WorldGenFeatureStateProvider> {

    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderSimpl> a = a("simple_state_provider", WorldGenFeatureStateProviderSimpl.b);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderWeighted> b = a("weighted_state_provider", WorldGenFeatureStateProviderWeighted.b);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderPlainFlower> c = a("plain_flower_provider", WorldGenFeatureStateProviderPlainFlower.b);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderForestFlower> d = a("forest_flower_provider", WorldGenFeatureStateProviderForestFlower.b);
    public static final WorldGenFeatureStateProviders<WorldGenFeatureStateProviderRotatedBlock> e = a("rotated_block_provider", WorldGenFeatureStateProviderRotatedBlock.b);
    private final Codec<P> f;

    private static <P extends WorldGenFeatureStateProvider> WorldGenFeatureStateProviders<P> a(String s, Codec<P> codec) {
        return (WorldGenFeatureStateProviders) IRegistry.a(IRegistry.BLOCK_STATE_PROVIDER_TYPE, s, (Object) (new WorldGenFeatureStateProviders<>(codec)));
    }

    private WorldGenFeatureStateProviders(Codec<P> codec) {
        this.f = codec;
    }

    public Codec<P> a() {
        return this.f;
    }
}
