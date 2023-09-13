package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.IRegistry;

public abstract class FeatureSize {

    public static final Codec<FeatureSize> a = IRegistry.FEATURE_SIZE_TYPE.dispatch(FeatureSize::b, FeatureSizeType::a);
    protected final OptionalInt b;

    protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> a() {
        return Codec.intRange(0, 80).optionalFieldOf("min_clipped_height").xmap((optional) -> {
            return (OptionalInt) optional.map(OptionalInt::of).orElse(OptionalInt.empty());
        }, (optionalint) -> {
            return optionalint.isPresent() ? Optional.of(optionalint.getAsInt()) : Optional.empty();
        }).forGetter((featuresize) -> {
            return featuresize.b;
        });
    }

    public FeatureSize(OptionalInt optionalint) {
        this.b = optionalint;
    }

    protected abstract FeatureSizeType<?> b();

    public abstract int a(int i, int j);

    public OptionalInt c() {
        return this.b;
    }
}
