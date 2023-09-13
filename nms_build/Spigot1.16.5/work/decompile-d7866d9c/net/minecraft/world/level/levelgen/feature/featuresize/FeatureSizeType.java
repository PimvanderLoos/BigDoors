package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class FeatureSizeType<P extends FeatureSize> {

    public static final FeatureSizeType<FeatureSizeTwoLayers> a = a("two_layers_feature_size", FeatureSizeTwoLayers.c);
    public static final FeatureSizeType<FeatureSizeThreeLayers> b = a("three_layers_feature_size", FeatureSizeThreeLayers.c);
    private final Codec<P> c;

    private static <P extends FeatureSize> FeatureSizeType<P> a(String s, Codec<P> codec) {
        return (FeatureSizeType) IRegistry.a(IRegistry.FEATURE_SIZE_TYPE, s, (Object) (new FeatureSizeType<>(codec)));
    }

    private FeatureSizeType(Codec<P> codec) {
        this.c = codec;
    }

    public Codec<P> a() {
        return this.c;
    }
}
