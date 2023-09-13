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

    public void tick() {
        if (this.canExpire()) {
            --this.timeToLive;
        }

    }

    public static <T> ExpirableMemory<T> of(T t0) {
        return new ExpirableMemory<>(t0, Long.MAX_VALUE);
    }

    public static <T> ExpirableMemory<T> of(T t0, long i) {
        return new ExpirableMemory<>(t0, i);
    }

    public long getTimeToLive() {
        return this.timeToLive;
    }

    public T getValue() {
        return this.value;
    }

    public boolean hasExpired() {
        return this.timeToLive <= 0L;
    }

    public String toString() {
        return this.value + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
    }

    @VisibleForDebug
    public boolean canExpire() {
        return this.timeToLive != Long.MAX_VALUE;
    }

    public static <T> Codec<ExpirableMemory<T>> codec(Codec<T> codec) {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf("value").forGetter((expirablememory) -> {
                return expirablememory.value;
            }), Codec.LONG.optionalFieldOf("ttl").forGetter((expirablememory) -> {
                return expirablememory.canExpire() ? Optional.of(expirablememory.timeToLive) : Optional.empty();
            })).apply(instance, (object, optional) -> {
                return new ExpirableMemory<>(object, (Long) optional.orElse(Long.MAX_VALUE));
            });
        });
    }
}
