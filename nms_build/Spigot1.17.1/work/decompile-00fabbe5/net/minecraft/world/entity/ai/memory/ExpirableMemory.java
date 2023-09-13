package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.VisibleForDebug;

public class ExpirableMemory<T> {

    private final T value;
    private long timeToLive;

    public ExpirableMemory(T t0, long i) {
        this.value = t0;
        this.timeToLive = i;
    }

    public void a() {
        if (this.e()) {
            --this.timeToLive;
        }

    }

    public static <T> ExpirableMemory<T> a(T t0) {
        return new ExpirableMemory<>(t0, Long.MAX_VALUE);
    }

    public static <T> ExpirableMemory<T> a(T t0, long i) {
        return new ExpirableMemory<>(t0, i);
    }

    public long b() {
        return this.timeToLive;
    }

    public T c() {
        return this.value;
    }

    public boolean d() {
        return this.timeToLive <= 0L;
    }

    public String toString() {
        return this.value + (this.e() ? " (ttl: " + this.timeToLive + ")" : "");
    }

    @VisibleForDebug
    public boolean e() {
        return this.timeToLive != Long.MAX_VALUE;
    }

    public static <T> Codec<ExpirableMemory<T>> a(Codec<T> codec) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf("value").forGetter((expirablememory) -> {
                return expirablememory.value;
            }), Codec.LONG.optionalFieldOf("ttl").forGetter((expirablememory) -> {
                return expirablememory.e() ? Optional.of(expirablememory.timeToLive) : Optional.empty();
            })).apply(instance, (object, optional) -> {
                return new ExpirableMemory<>(object, (Long) optional.orElse(Long.MAX_VALUE));
            });
        });
    }
}
