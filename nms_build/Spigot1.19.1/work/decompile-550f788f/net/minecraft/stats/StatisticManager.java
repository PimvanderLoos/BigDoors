package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.EntityHuman;

public class StatisticManager {

    protected final Object2IntMap<Statistic<?>> stats = Object2IntMaps.synchronize(new Object2IntOpenHashMap());

    public StatisticManager() {
        this.stats.defaultReturnValue(0);
    }

    public void increment(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        int j = (int) Math.min((long) this.getValue(statistic) + (long) i, 2147483647L);

        this.setValue(entityhuman, statistic, j);
    }

    public void setValue(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        this.stats.put(statistic, i);
    }

    public <T> int getValue(StatisticWrapper<T> statisticwrapper, T t0) {
        return statisticwrapper.contains(t0) ? this.getValue(statisticwrapper.get(t0)) : 0;
    }

    public int getValue(Statistic<?> statistic) {
        return this.stats.getInt(statistic);
    }
}
