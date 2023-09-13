package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class ExpirableMemory<T> {

    private final T a;
    private long b;

    public ExpirableMemory(T t0, long i) {
        this.a = t0;
        this.b = i;
    }

    public void a() {
        if (this.e()) {
            --this.b;
        }

    }

    public static <T> ExpirableMemory<T> a(T t0) {
        return new ExpirableMemory<>(t0, Long.MAX_VALUE);
    }

    public static <T> ExpirableMemory<T> a(T t0, long i) {
        return new ExpirableMemory<>(t0, i);
    }

    public T c() {
        return this.a;
    }

    public boolean d() {
        return this.b <= 0L;
    }

    public String toString() {
        return this.a.toString() + (this.e() ? " (ttl: " + this.b + ")" : "");
    }

    public boolean e() {
        return this.b != Long.MAX_VALUE;
    }

    public static <T> Codec<ExpirableMemory<T>> a(Codec<T> codec) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf("value").forGetter((expirablememory) -> {
                return expirablememory.a;
            }), Codec.LONG.optionalFieldOf("ttl").forGetter((expirablememory) -> {
                return expirablememory.e() ? Optional.of(expirablememory.b) : Optional.empty();
            })).apply(instance, (object, optional) -> {
                return new ExpirableMemory<>(object, (Long) optional.orElse(Long.MAX_VALUE));
            });
        });
    }
}
