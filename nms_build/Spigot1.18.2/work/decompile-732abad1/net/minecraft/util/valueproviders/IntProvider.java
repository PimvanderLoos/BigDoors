package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.IRegistry;

public abstract class IntProvider {

    private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(Codec.INT, IRegistry.INT_PROVIDER_TYPES.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec));
    public static final Codec<IntProvider> CODEC = IntProvider.CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> {
        return (IntProvider) either.map(ConstantInt::of, (intprovider) -> {
            return intprovider;
        });
    }, (intprovider) -> {
        return intprovider.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantInt) intprovider).getValue()) : Either.right(intprovider);
    });
    public static final Codec<IntProvider> NON_NEGATIVE_CODEC = codec(0, Integer.MAX_VALUE);
    public static final Codec<IntProvider> POSITIVE_CODEC = codec(1, Integer.MAX_VALUE);

    public IntProvider() {}

    public static Codec<IntProvider> codec(int i, int j) {
        Function<IntProvider, DataResult<IntProvider>> function = (intprovider) -> {
            return intprovider.getMinValue() < i ? DataResult.error("Value provider too low: " + i + " [" + intprovider.getMinValue() + "-" + intprovider.getMaxValue() + "]") : (intprovider.getMaxValue() > j ? DataResult.error("Value provider too high: " + j + " [" + intprovider.getMinValue() + "-" + intprovider.getMaxValue() + "]") : DataResult.success(intprovider));
        };

        return IntProvider.CODEC.flatXmap(function, function);
    }

    public abstract int sample(Random random);

    public abstract int getMinValue();

    public abstract int getMaxValue();

    public abstract IntProviderType<?> getType();
}
