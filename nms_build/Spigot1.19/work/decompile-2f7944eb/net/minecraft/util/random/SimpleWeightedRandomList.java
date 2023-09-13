package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class SimpleWeightedRandomList<E> extends WeightedRandomList<WeightedEntry.b<E>> {

    public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodecAllowingEmpty(Codec<E> codec) {
        return WeightedEntry.b.codec(codec).listOf().xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
    }

    public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodec(Codec<E> codec) {
        return ExtraCodecs.nonEmptyList(WeightedEntry.b.codec(codec).listOf()).xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
    }

    SimpleWeightedRandomList(List<? extends WeightedEntry.b<E>> list) {
        super(list);
    }

    public static <E> SimpleWeightedRandomList.a<E> builder() {
        return new SimpleWeightedRandomList.a<>();
    }

    public static <E> SimpleWeightedRandomList<E> empty() {
        return new SimpleWeightedRandomList<>(List.of());
    }

    public static <E> SimpleWeightedRandomList<E> single(E e0) {
        return new SimpleWeightedRandomList<>(List.of(WeightedEntry.wrap(e0, 1)));
    }

    public Optional<E> getRandomValue(RandomSource randomsource) {
        return this.getRandom(randomsource).map(WeightedEntry.b::getData);
    }

    public static class a<E> {

        private final Builder<WeightedEntry.b<E>> result = ImmutableList.builder();

        public a() {}

        public SimpleWeightedRandomList.a<E> add(E e0, int i) {
            this.result.add(WeightedEntry.wrap(e0, i));
            return this;
        }

        public SimpleWeightedRandomList<E> build() {
            return new SimpleWeightedRandomList<>(this.result.build());
        }
    }
}
