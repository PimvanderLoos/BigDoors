package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class WeightedListHeight extends HeightProvider {

    public static final Codec<WeightedListHeight> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SimpleWeightedRandomList.wrappedCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter((weightedlistheight) -> {
            return weightedlistheight.distribution;
        })).apply(instance, WeightedListHeight::new);
    });
    private final SimpleWeightedRandomList<HeightProvider> distribution;

    public WeightedListHeight(SimpleWeightedRandomList<HeightProvider> simpleweightedrandomlist) {
        this.distribution = simpleweightedrandomlist;
    }

    @Override
    public int sample(RandomSource randomsource, WorldGenerationContext worldgenerationcontext) {
        return ((HeightProvider) this.distribution.getRandomValue(randomsource).orElseThrow(IllegalStateException::new)).sample(randomsource, worldgenerationcontext);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.WEIGHTED_LIST;
    }
}
