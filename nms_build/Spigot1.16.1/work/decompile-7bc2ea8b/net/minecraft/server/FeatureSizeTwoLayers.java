package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class FeatureSizeTwoLayers extends FeatureSize {

    public static final Codec<FeatureSizeTwoLayers> c = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("limit").withDefault(1).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.d;
        }), Codec.INT.fieldOf("lower_size").withDefault(0).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.e;
        }), Codec.INT.fieldOf("upper_size").withDefault(1).forGetter((featuresizetwolayers) -> {
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
