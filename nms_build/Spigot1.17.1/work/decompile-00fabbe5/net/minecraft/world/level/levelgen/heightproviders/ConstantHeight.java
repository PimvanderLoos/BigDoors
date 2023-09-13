package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {

    public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.a(0));
    public static final Codec<ConstantHeight> CODEC = Codec.either(VerticalAnchor.CODEC, RecordCodecBuilder.create((instance) -> {
        return instance.group(VerticalAnchor.CODEC.fieldOf("value").forGetter((constantheight) -> {
            return constantheight.value;
        })).apply(instance, ConstantHeight::new);
    })).xmap((either) -> {
        return (ConstantHeight) either.map(ConstantHeight::a, (constantheight) -> {
            return constantheight;
        });
    }, (constantheight) -> {
        return Either.left(constantheight.value);
    });
    private final VerticalAnchor value;

    public static ConstantHeight a(VerticalAnchor verticalanchor) {
        return new ConstantHeight(verticalanchor);
    }

    private ConstantHeight(VerticalAnchor verticalanchor) {
        this.value = verticalanchor;
    }

    public VerticalAnchor b() {
        return this.value;
    }

    @Override
    public int a(Random random, WorldGenerationContext worldgenerationcontext) {
        return this.value.a(worldgenerationcontext);
    }

    @Override
    public HeightProviderType<?> a() {
        return HeightProviderType.CONSTANT;
    }

    public String toString() {
        return this.value.toString();
    }
}
