package net.minecraft.util.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public interface WeightedEntry {

    Weight a();

    static <T> WeightedEntry.b<T> a(T t0, int i) {
        return new WeightedEntry.b<>(t0, Weight.a(i));
    }

    public static class b<T> implements WeightedEntry {

        private final T data;
        private final Weight weight;

        b(T t0, Weight weight) {
            this.data = t0;
            this.weight = weight;
        }

        public T b() {
            return this.data;
        }

        @Override
        public Weight a() {
            return this.weight;
        }

        public static <E> Codec<WeightedEntry.b<E>> a(Codec<E> codec) {
            return RecordCodecBuilder.create((instance) -> {
                return instance.group(codec.fieldOf("data").forGetter(WeightedEntry.b::b), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.b::a)).apply(instance, WeightedEntry.b::new);
            });
        }
    }

    public static class a implements WeightedEntry {

        private final Weight weight;

        public a(int i) {
            this.weight = Weight.a(i);
        }

        public a(Weight weight) {
            this.weight = weight;
        }

        @Override
        public Weight a() {
            return this.weight;
        }
    }
}
