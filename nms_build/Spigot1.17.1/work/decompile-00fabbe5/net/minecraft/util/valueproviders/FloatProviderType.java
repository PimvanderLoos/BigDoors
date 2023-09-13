package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface FloatProviderType<P extends FloatProvider> {

    FloatProviderType<ConstantFloat> CONSTANT = a("constant", ConstantFloat.CODEC);
    FloatProviderType<UniformFloat> UNIFORM = a("uniform", UniformFloat.CODEC);
    FloatProviderType<ClampedNormalFloat> CLAMPED_NORMAL = a("clamped_normal", ClampedNormalFloat.CODEC);
    FloatProviderType<TrapezoidFloat> TRAPEZOID = a("trapezoid", TrapezoidFloat.CODEC);

    Codec<P> codec();

    static <P extends FloatProvider> FloatProviderType<P> a(String s, Codec<P> codec) {
        return (FloatProviderType) IRegistry.a(IRegistry.FLOAT_PROVIDER_TYPES, s, (Object) (() -> {
            return codec;
        }));
    }
}
