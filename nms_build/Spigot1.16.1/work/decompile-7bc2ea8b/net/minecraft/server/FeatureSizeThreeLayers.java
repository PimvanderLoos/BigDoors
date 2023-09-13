package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class FeatureSizeThreeLayers extends FeatureSize {

    public static final Codec<FeatureSizeThreeLayers> c = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("limit").withDefault(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.d;
        }), Codec.INT.fieldOf("upper_limit").withDefault(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.e;
        }), Codec.INT.fieldOf("lower_size").withDefault(0).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.f;
        }), Codec.INT.fieldOf("middle_size").withDefault(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.g;
        }), Codec.INT.fieldOf("upper_size").withDefault(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.h;
        }), a()).apply(instance, FeatureSizeThreeLayers::new);
    });
    private final int d;
    private final int e;
    private final int f;
    private final int g;
    private final int h;

    public FeatureSizeThreeLayers(int i, int j, int k, int l, int i1, OptionalInt optionalint) {
        super(optionalint);
        this.d = i;
        this.e = j;
        this.f = k;
        this.g = l;
        this.h = i1;
    }

    @Override
    protected FeatureSizeType<?> b() {
        return FeatureSizeType.b;
    }

    @Override
    public int a(int i, int j) {
        return j < this.d ? this.f : (j >= i - this.e ? this.h : this.g);
    }
}
