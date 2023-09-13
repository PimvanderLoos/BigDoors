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

    public void b(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        int j = (int) Math.min((long) this.getStatisticValue(statistic) + (long) i, 2147483647L);

        this.setStatistic(entityhuman, statistic, j);
    }

    public void setStatistic(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        this.stats.put(statistic, i);
    }

    public <T> int a(StatisticWrapper<T> statisticwrapper, T t0) {
        return statisticwrapper.a(t0) ? this.getStatisticValue(statisticwrapper.b(t0)) : 0;
    }

    public int getStatisticValue(Statistic<?> statistic) {
        return this.stats.getInt(statistic);
    }
}
