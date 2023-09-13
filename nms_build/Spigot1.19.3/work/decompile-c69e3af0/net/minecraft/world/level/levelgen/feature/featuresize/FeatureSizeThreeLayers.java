package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class FeatureSizeThreeLayers extends FeatureSize {

    public static final Codec<FeatureSizeThreeLayers> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 80).fieldOf("limit").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.limit;
        }), Codec.intRange(0, 80).fieldOf("upper_limit").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.upperLimit;
        }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.lowerSize;
        }), Codec.intRange(0, 16).fieldOf("middle_size").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.middleSize;
        }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((featuresizethreelayers) -> {
            return featuresizethreelayers.upperSize;
        }), minClippedHeightCodec()).apply(instance, FeatureSizeThreeLayers::new);
    });
    private final int limit;
    private final int upperLimit;
    private final int lowerSize;
    private final int middleSize;
    private final int upperSize;

    public FeatureSizeThreeLayers(int i, int j, int k, int l, int i1, OptionalInt optionalint) {
        super(optionalint);
        this.limit = i;
        this.upperLimit = j;
        this.lowerSize = k;
        this.middleSize = l;
        this.upperSize = i1;
    }

    @Override
    protected FeatureSizeType<?> type() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int getSizeAtHeight(int i, int j) {
        return j < this.limit ? this.lowerSize : (j >= i - this.upperLimit ? this.upperSize : this.middleSize);
    }
}
