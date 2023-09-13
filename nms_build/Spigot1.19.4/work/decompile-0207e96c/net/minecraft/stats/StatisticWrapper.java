package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.IChatBaseComponent;

public class StatisticWrapper<T> implements Iterable<Statistic<T>> {

    private final IRegistry<T> registry;
    private final Map<T, Statistic<T>> map = new IdentityHashMap();
    @Nullable
    private IChatBaseComponent displayName;

    public StatisticWrapper(IRegistry<T> iregistry) {
        this.registry = iregistry;
    }

    public boolean contains(T t0) {
        return this.map.containsKey(t0);
    }

    public Statistic<T> get(T t0, Counter counter) {
        return (Statistic) this.map.computeIfAbsent(t0, (object) -> {
            return new Statistic<>(this, object, counter);
        });
    }

    public IRegistry<T> getRegistry() {
        return this.registry;
    }

    public Iterator<Statistic<T>> iterator() {
        return this.map.values().iterator();
    }

    public Statistic<T> get(T t0) {
        return this.get(t0, Counter.DEFAULT);
    }

    public String getTranslationKey() {
        String s = BuiltInRegistries.STAT_TYPE.getKey(this).toString();

        return "stat_type." + s.replace(':', '.');
    }

    public IChatBaseComponent getDisplayName() {
        if (this.displayName == null) {
            this.displayName = IChatBaseComponent.translatable(this.getTranslationKey());
        }

        return this.displayName;
    }
}
