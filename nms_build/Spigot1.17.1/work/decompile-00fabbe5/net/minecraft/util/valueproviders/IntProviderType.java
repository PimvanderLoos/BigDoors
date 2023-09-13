package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface IntProviderType<P extends IntProvider> {

    IntProviderType<ConstantInt> CONSTANT = a("constant", ConstantInt.CODEC);
    IntProviderType<UniformInt> UNIFORM = a("uniform", UniformInt.CODEC);
    IntProviderType<BiasedToBottomInt> BIASED_TO_BOTTOM = a("biased_to_bottom", BiasedToBottomInt.CODEC);
    IntProviderType<ClampedInt> CLAMPED = a("clamped", ClampedInt.CODEC);

    Codec<P> codec();

    static <P extends IntProvider> IntProviderType<P> a(String s, Codec<P> codec) {
        return (IntProviderType) IRegistry.a(IRegistry.INT_PROVIDER_TYPES, s, (Object) (() -> {
            return codec;
        }));
    }
}
