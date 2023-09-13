package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public abstract class FloatProvider {

    private static final Codec<Either<Float, FloatProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.FLOAT, IRegistry.FLOAT_PROVIDER_TYPES.byNameCodec().dispatch(FloatProvider::getType, FloatProviderType::codec));
    public static final Codec<FloatProvider> CODEC = FloatProvider.CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> {
        return (FloatProvider) either.map(ConstantFloat::of, (floatprovider) -> {
            return floatprovider;
        });
    }, (floatprovider) -> {
        return floatprovider.getType() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloat) floatprovider).getValue()) : Either.right(floatprovider);
    });

    public FloatProvider() {}

    public static Codec<FloatProvider> codec(float f, float f1) {
        Function<FloatProvider, DataResult<FloatProvider>> function = (floatprovider) -> {
            return floatprovider.getMinValue() < f ? DataResult.error("Value provider too low: " + f + " [" + floatprovider.getMinValue() + "-" + floatprovider.getMaxValue() + "]") : (floatprovider.getMaxValue() > f1 ? DataResult.error("Value provider too high: " + f1 + " [" + floatprovider.getMinValue() + "-" + floatprovider.getMaxValue() + "]") : DataResult.success(floatprovider));
        };

        return FloatProvider.CODEC.flatXmap(function, function);
    }

    public abstract float sample(Random random);

    public abstract float getMinValue();

    public abstract float getMaxValue();

    public abstract FloatProviderType<?> getType();
}
