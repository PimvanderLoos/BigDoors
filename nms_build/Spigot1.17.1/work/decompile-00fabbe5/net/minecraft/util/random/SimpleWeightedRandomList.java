package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SimpleWeightedRandomList<E> extends WeightedRandomList<WeightedEntry.b<E>> {

    public static <E> Codec<SimpleWeightedRandomList<E>> a(Codec<E> codec) {
        return WeightedEntry.b.a(codec).listOf().xmap(SimpleWeightedRandomList::new, WeightedRandomList::d);
    }

    SimpleWeightedRandomList(List<? extends WeightedEntry.b<E>> list) {
        super(list);
    }

    public static <E> SimpleWeightedRandomList.a<E> a() {
        return new SimpleWeightedRandomList.a<>();
    }

    public Optional<E> a(Random random) {
        return this.b(random).map(WeightedEntry.b::b);
    }

    public static class a<E> {

        private final Builder<WeightedEntry.b<E>> result = ImmutableList.builder();

        public a() {}

        public SimpleWeightedRandomList.a<E> a(E e0, int i) {
            this.result.add(WeightedEntry.a(e0, i));
            return this;
        }

        public SimpleWeightedRandomList<E> a() {
            return new SimpleWeightedRandomList<>(this.result.build());
        }
    }
}
