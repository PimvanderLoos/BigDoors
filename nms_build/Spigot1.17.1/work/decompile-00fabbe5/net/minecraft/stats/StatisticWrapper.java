package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;

public class StatisticWrapper<T> implements Iterable<Statistic<T>> {

    private final IRegistry<T> registry;
    private final Map<T, Statistic<T>> map = new IdentityHashMap();
    @Nullable
    private IChatBaseComponent displayName;

    public StatisticWrapper(IRegistry<T> iregistry) {
        this.registry = iregistry;
    }

    public boolean a(T t0) {
        return this.map.containsKey(t0);
    }

    public Statistic<T> a(T t0, Counter counter) {
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

    public Statistic<T> b(T t0) {
        return this.a(t0, Counter.DEFAULT);
    }

    public String b() {
        String s = IRegistry.STAT_TYPE.getKey(this).toString();

        return "stat_type." + s.replace(':', '.');
    }

    public IChatBaseComponent c() {
        if (this.displayName == null) {
            this.displayName = new ChatMessage(this.b());
        }

        return this.displayName;
    }
}
