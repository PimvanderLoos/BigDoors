package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class FeatureSizeTwoLayers extends FeatureSize {

    public static final Codec<FeatureSizeTwoLayers> c = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 81).fieldOf("limit").orElse(1).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.d;
        }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.e;
        }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.f;
        }), a()).apply(instance, FeatureSizeTwoLayers::new);
    });
    private final int d;
    private final int e;
    private final int f;

    public FeatureSizeTwoLayers(int i, int j, int k) {
        this(i, j, k, OptionalInt.empty());
    }

    public FeatureSizeTwoLayers(int i, int j, int k, OptionalInt optionalint) {
        super(optionalint);
        this.d = i;
        this.e = j;
        this.f = k;
    }

    @Override
    protected FeatureSizeType<?> b() {
        return FeatureSizeType.a;
    }

    @Override
    public int a(int i, int j) {
        return j < this.d ? this.e : this.f;
    }
}
