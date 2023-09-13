package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class FeatureSizeThreeLayers extends FeatureSize {

    public static final Codec<FeatureSizeThreeLayers> c = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 80).fieldOf("limit").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.d;
        }), Codec.intRange(0, 80).fieldOf("upper_limit").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.e;
        }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.f;
        }), Codec.intRange(0, 16).fieldOf("middle_size").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.g;
        }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((featuresizethreelayers) -> {
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
