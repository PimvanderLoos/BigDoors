package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public abstract class IntProvider {

    private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, IRegistry.INT_PROVIDER_TYPES.dispatch(IntProvider::c, IntProviderType::codec));
    public static final Codec<IntProvider> CODEC = IntProvider.CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> {
        return (IntProvider) either.map(ConstantInt::a, (intprovider) -> {
            return intprovider;
        });
    }, (intprovider) -> {
        return intprovider.c() == IntProviderType.CONSTANT ? Either.left(((ConstantInt) intprovider).d()) : Either.right(intprovider);
    });
    public static final Codec<IntProvider> NON_NEGATIVE_CODEC = b(0, Integer.MAX_VALUE);
    public static final Codec<IntProvider> POSITIVE_CODEC = b(1, Integer.MAX_VALUE);

    public IntProvider() {}

    public static Codec<IntProvider> b(int i, int j) {
        Function<IntProvider, DataResult<IntProvider>> function = (intprovider) -> {
            return intprovider.a() < i ? DataResult.error("Value provider too low: " + i + " [" + intprovider.a() + "-" + intprovider.b() + "]") : (intprovider.b() > j ? DataResult.error("Value provider too high: " + j + " [" + intprovider.a() + "-" + intprovider.b() + "]") : DataResult.success(intprovider));
        };

        return IntProvider.CODEC.flatXmap(function, function);
    }

    public abstract int a(Random random);

    public abstract int a();

    public abstract int b();

    public abstract IntProviderType<?> c();
}
