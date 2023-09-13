package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class FeatureSizeTwoLayers extends FeatureSize {

    public static final Codec<FeatureSizeTwoLayers> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, 81).fieldOf("limit").orElse(1).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.limit;
        }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.lowerSize;
        }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((featuresizetwolayers) -> {
            return featuresizetwolayers.upperSize;
        }), minClippedHeightCodec()).apply(instance, FeatureSizeTwoLayers::new);
    });
    private final int limit;
    private final int lowerSize;
    private final int upperSize;

    public FeatureSizeTwoLayers(int i, int j, int k) {
        this(i, j, k, OptionalInt.empty());
    }

    public FeatureSizeTwoLayers(int i, int j, int k, OptionalInt optionalint) {
        super(optionalint);
        this.limit = i;
        this.lowerSize = j;
        this.upperSize = k;
    }

    @Override
    protected FeatureSizeType<?> type() {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int getSizeAtHeight(int i, int j) {
        return j < this.limit ? this.lowerSize : this.upperSize;
    }
}
