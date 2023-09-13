package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {

    public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
    public static final Codec<ConstantHeight> CODEC = Codec.either(VerticalAnchor.CODEC, RecordCodecBuilder.create((instance) -> {
        return instance.group(VerticalAnchor.CODEC.fieldOf("value").forGetter((constantheight) -> {
            return constantheight.value;
        })).apply(instance, ConstantHeight::new);
    })).xmap((either) -> {
        return (ConstantHeight) either.map(ConstantHeight::of, (constantheight) -> {
            return constantheight;
        });
    }, (constantheight) -> {
        return Either.left(constantheight.value);
    });
    private final VerticalAnchor value;

    public static ConstantHeight of(VerticalAnchor verticalanchor) {
        return new ConstantHeight(verticalanchor);
    }

    private ConstantHeight(VerticalAnchor verticalanchor) {
        this.value = verticalanchor;
    }

    public VerticalAnchor getValue() {
        return this.value;
    }

    @Override
    public int sample(RandomSource randomsource, WorldGenerationContext worldgenerationcontext) {
        return this.value.resolveY(worldgenerationcontext);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.CONSTANT;
    }

    public String toString() {
        return this.value.toString();
    }
}
