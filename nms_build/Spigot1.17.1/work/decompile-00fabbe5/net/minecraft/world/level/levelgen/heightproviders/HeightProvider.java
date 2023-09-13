package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public abstract class HeightProvider {

    private static final Codec<Either<VerticalAnchor, HeightProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(VerticalAnchor.CODEC, IRegistry.HEIGHT_PROVIDER_TYPES.dispatch(HeightProvider::a, HeightProviderType::codec));
    public static final Codec<HeightProvider> CODEC = HeightProvider.CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> {
        return (HeightProvider) either.map(ConstantHeight::a, (heightprovider) -> {
            return heightprovider;
        });
    }, (heightprovider) -> {
        return heightprovider.a() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeight) heightprovider).b()) : Either.right(heightprovider);
    });

    public HeightProvider() {}

    public abstract int a(Random random, WorldGenerationContext worldgenerationcontext);

    public abstract HeightProviderType<?> a();
}
