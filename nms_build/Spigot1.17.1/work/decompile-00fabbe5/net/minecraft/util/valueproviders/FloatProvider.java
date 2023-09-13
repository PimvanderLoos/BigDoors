package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public abstract class FloatProvider {

    private static final Codec<Either<Float, FloatProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.FLOAT, IRegistry.FLOAT_PROVIDER_TYPES.dispatch(FloatProvider::c, FloatProviderType::codec));
    public static final Codec<FloatProvider> CODEC = FloatProvider.CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> {
        return (FloatProvider) either.map(ConstantFloat::a, (floatprovider) -> {
            return floatprovider;
        });
    }, (floatprovider) -> {
        return floatprovider.c() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloat) floatprovider).d()) : Either.right(floatprovider);
    });

    public FloatProvider() {}

    public static Codec<FloatProvider> a(float f, float f1) {
        Function<FloatProvider, DataResult<FloatProvider>> function = (floatprovider) -> {
            return floatprovider.a() < f ? DataResult.error("Value provider too low: " + f + " [" + floatprovider.a() + "-" + floatprovider.b() + "]") : (floatprovider.b() > f1 ? DataResult.error("Value provider too high: " + f1 + " [" + floatprovider.a() + "-" + floatprovider.b() + "]") : DataResult.success(floatprovider));
        };

        return FloatProvider.CODEC.flatXmap(function, function);
    }

    public abstract float a(Random random);

    public abstract float a();

    public abstract float b();

    public abstract FloatProviderType<?> c();
}
