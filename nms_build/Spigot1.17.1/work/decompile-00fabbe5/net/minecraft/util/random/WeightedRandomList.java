package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class WeightedRandomList<E extends WeightedEntry> {

    private final int totalWeight;
    private final ImmutableList<E> items;

    WeightedRandomList(List<? extends E> list) {
        this.items = ImmutableList.copyOf(list);
        this.totalWeight = WeightedRandom2.a(list);
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> b() {
        return new WeightedRandomList<>(ImmutableList.of());
    }

    @SafeVarargs
    public static <E extends WeightedEntry> WeightedRandomList<E> a(E... ae) {
        return new WeightedRandomList<>(ImmutableList.copyOf(ae));
    }

    public static <E extends WeightedEntry> WeightedRandomList<E> a(List<E> list) {
        return new WeightedRandomList<>(list);
    }

    public boolean c() {
        return this.items.isEmpty();
    }

    public Optional<E> b(Random random) {
        if (this.totalWeight == 0) {
            return Optional.empty();
        } else {
            int i = random.nextInt(this.totalWeight);

            return WeightedRandom2.a(this.items, i);
        }
    }

    public List<E> d() {
        return this.items;
    }

    public static <E extends WeightedEntry> Codec<WeightedRandomList<E>> b(Codec<E> codec) {
        return codec.listOf().xmap(WeightedRandomList::a, WeightedRandomList::d);
    }
}
