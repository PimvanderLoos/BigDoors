package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;

public class ShufflingList<U> implements Iterable<U> {

    protected final List<ShufflingList.a<U>> entries;
    private final RandomSource random = RandomSource.create();

    public ShufflingList() {
        this.entries = Lists.newArrayList();
    }

    private ShufflingList(List<ShufflingList.a<U>> list) {
        this.entries = Lists.newArrayList(list);
    }

    public static <U> Codec<ShufflingList<U>> codec(Codec<U> codec) {
        return ShufflingList.a.codec(codec).listOf().xmap(ShufflingList::new, (shufflinglist) -> {
            return shufflinglist.entries;
        });
    }

    public ShufflingList<U> add(U u0, int i) {
        this.entries.add(new ShufflingList.a<>(u0, i));
        return this;
    }

    public ShufflingList<U> shuffle() {
        this.entries.forEach((shufflinglist_a) -> {
            shufflinglist_a.setRandom(this.random.nextFloat());
        });
        this.entries.sort(Comparator.comparingDouble(ShufflingList.a::getRandWeight));
        return this;
    }

    public Stream<U> stream() {
        return this.entries.stream().map(ShufflingList.a::getData);
    }

    public Iterator<U> iterator() {
        return Iterators.transform(this.entries.iterator(), ShufflingList.a::getData);
    }

    public String toString() {
        return "ShufflingList[" + this.entries + "]";
    }

    public static class a<T> {

        final T data;
        final int weight;
        private double randWeight;

        a(T t0, int i) {
            this.weight = i;
            this.data = t0;
        }

        private double getRandWeight() {
            return this.randWeight;
        }

        void setRandom(float f) {
            this.randWeight = -Math.pow((double) f, (double) (1.0F / (float) this.weight));
        }

        public T getData() {
            return this.data;
        }

        public int getWeight() {
            return this.weight;
        }

        public String toString() {
            return this.weight + ":" + this.data;
        }

        public static <E> Codec<ShufflingList.a<E>> codec(final Codec<E> codec) {
            return new Codec<ShufflingList.a<E>>() {
                public <T> DataResult<Pair<ShufflingList.a<E>, T>> decode(DynamicOps<T> dynamicops, T t0) {
                    Dynamic<T> dynamic = new Dynamic(dynamicops, t0);
                    OptionalDynamic optionaldynamic = dynamic.get("data");
                    Codec codec1 = codec;

                    Objects.requireNonNull(codec);
                    return optionaldynamic.flatMap(codec1::parse).map((object) -> {
                        return new ShufflingList.a<>(object, dynamic.get("weight").asInt(1));
                    }).map((shufflinglist_a) -> {
                        return Pair.of(shufflinglist_a, dynamicops.empty());
                    });
                }

                public <T> DataResult<T> encode(ShufflingList.a<E> shufflinglist_a, DynamicOps<T> dynamicops, T t0) {
                    return dynamicops.mapBuilder().add("weight", dynamicops.createInt(shufflinglist_a.weight)).add("data", codec.encodeStart(dynamicops, shufflinglist_a.data)).build(t0);
                }
            };
        }
    }
}
