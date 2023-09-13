package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

public class WeightedListInt extends IntProvider {

    public static final Codec<WeightedListInt> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SimpleWeightedRandomList.wrappedCodec(IntProvider.CODEC).fieldOf("distribution").forGetter((weightedlistint) -> {
            return weightedlistint.distribution;
        })).apply(instance, WeightedListInt::new);
    });
    private final SimpleWeightedRandomList<IntProvider> distribution;
    private final int minValue;
    private final int maxValue;

    public WeightedListInt(SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist) {
        this.distribution = simpleweightedrandomlist;
        List<WeightedEntry.b<IntProvider>> list = simpleweightedrandomlist.unwrap();
        int i = Integer.MAX_VALUE;
        int j = Integer.MIN_VALUE;

        int k;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); j = Math.max(j, k)) {
            WeightedEntry.b<IntProvider> weightedentry_b = (WeightedEntry.b) iterator.next();
            int l = ((IntProvider) weightedentry_b.getData()).getMinValue();

            k = ((IntProvider) weightedentry_b.getData()).getMaxValue();
            i = Math.min(i, l);
        }

        this.minValue = i;
        this.maxValue = j;
    }

    @Override
    public int sample(RandomSource randomsource) {
        return ((IntProvider) this.distribution.getRandomValue(randomsource).orElseThrow(IllegalStateException::new)).sample(randomsource);
    }

    @Override
    public int getMinValue() {
        return this.minValue;
    }

    @Override
    public int getMaxValue() {
        return this.maxValue;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.WEIGHTED_LIST;
    }
}
