package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class FeatureSizeType<P extends FeatureSize> {

    public static final FeatureSizeType<FeatureSizeTwoLayers> TWO_LAYERS_FEATURE_SIZE = a("two_layers_feature_size", FeatureSizeTwoLayers.CODEC);
    public static final FeatureSizeType<FeatureSizeThreeLayers> THREE_LAYERS_FEATURE_SIZE = a("three_layers_feature_size", FeatureSizeThreeLayers.CODEC);
    private final Codec<P> codec;

    private static <P extends FeatureSize> FeatureSizeType<P> a(String s, Codec<P> codec) {
        return (FeatureSizeType) IRegistry.a(IRegistry.FEATURE_SIZE_TYPES, s, (Object) (new FeatureSizeType<>(codec)));
    }

    private FeatureSizeType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> a() {
        return this.codec;
    }
}
