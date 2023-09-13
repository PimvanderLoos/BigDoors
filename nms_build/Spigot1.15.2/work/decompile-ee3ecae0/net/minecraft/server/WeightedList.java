package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class WeightedList<U> {

    protected final List<WeightedList<U>.a<? extends U>> a;
    private final Random b;

    public WeightedList(Random random) {
        this.a = Lists.newArrayList();
        this.b = random;
    }

    public WeightedList() {
        this(new Random());
    }

    public <T> WeightedList(Dynamic<T> dynamic, Function<Dynamic<T>, U> function) {
        this();
        dynamic.asStream().forEach((dynamic1) -> {
            dynamic1.get("data").map((dynamic2) -> {
                U u0 = function.apply(dynamic2);
                int i = dynamic1.get("weight").asInt(1);

                return this.a(u0, i);
            });
        });
    }

    public <T> T a(DynamicOps<T> dynamicops, Function<U, Dynamic<T>> function) {
        return dynamicops.createList(this.c().map((weightedlist_a) -> {
            return dynamicops.createMap(ImmutableMap.builder().put(dynamicops.createString("data"), ((Dynamic) function.apply(weightedlist_a.a())).getValue()).put(dynamicops.createString("weight"), dynamicops.createInt(weightedlist_a.b())).build());
        }));
    }

    public WeightedList<U> a(U u0, int i) {
        this.a.add(new WeightedList.a<>(u0, i));
        return this;
    }

    public WeightedList<U> a() {
        return this.a(this.b);
    }

    public WeightedList<U> a(Random random) {
        this.a.forEach((weightedlist_a) -> {
            weightedlist_a.a(random.nextFloat());
        });
        this.a.sort(Comparator.comparingDouble((object) -> {
            return ((WeightedList.a) object).c();
        }));
        return this;
    }

    public Stream<? extends U> b() {
        return this.a.stream().map(WeightedList.a::a);
    }

    public Stream<WeightedList<U>.a<? extends U>> c() {
        return this.a.stream();
    }

    public U b(Random random) {
        return this.a(random).b().findFirst().orElseThrow(RuntimeException::new);
    }

    public String toString() {
        return "WeightedList[" + this.a + "]";
    }

    public class a<T> {

        private final T b;
        private final int c;
        private double d;

        private a(Object object, int i) {
            this.c = i;
            this.b = object;
        }

        private double c() {
            return this.d;
        }

        private void a(float f) {
            this.d = -Math.pow((double) f, (double) (1.0F / (float) this.c));
        }

        public T a() {
            return this.b;
        }

        public int b() {
            return this.c;
        }

        public String toString() {
            return "" + this.c + ":" + this.b;
        }
    }
}
