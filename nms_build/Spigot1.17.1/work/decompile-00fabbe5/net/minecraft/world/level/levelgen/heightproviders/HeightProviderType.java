package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public interface HeightProviderType<P extends HeightProvider> {

    HeightProviderType<ConstantHeight> CONSTANT = a("constant", ConstantHeight.CODEC);
    HeightProviderType<UniformHeight> UNIFORM = a("uniform", UniformHeight.CODEC);
    HeightProviderType<BiasedToBottomHeight> BIASED_TO_BOTTOM = a("biased_to_bottom", BiasedToBottomHeight.CODEC);
    HeightProviderType<VeryBiasedToBottomHeight> VERY_BIASED_TO_BOTTOM = a("very_biased_to_bottom", VeryBiasedToBottomHeight.CODEC);
    HeightProviderType<TrapezoidHeight> TRAPEZOID = a("trapezoid", TrapezoidHeight.CODEC);

    Codec<P> codec();

    static <P extends HeightProvider> HeightProviderType<P> a(String s, Codec<P> codec) {
        return (HeightProviderType) IRegistry.a(IRegistry.HEIGHT_PROVIDER_TYPES, s, (Object) (() -> {
            return codec;
        }));
    }
}
