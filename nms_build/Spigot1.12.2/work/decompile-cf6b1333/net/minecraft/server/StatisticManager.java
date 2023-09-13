package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

public class StatisticManager {

    protected final Map<Statistic, StatisticWrapper> a = Maps.newConcurrentMap();

    public StatisticManager() {}

    public void b(EntityHuman entityhuman, Statistic statistic, int i) {
        this.setStatistic(entityhuman, statistic, this.getStatisticValue(statistic) + i);
    }

    public void setStatistic(EntityHuman entityhuman, Statistic statistic, int i) {
        StatisticWrapper statisticwrapper = (StatisticWrapper) this.a.get(statistic);

        if (statisticwrapper == null) {
            statisticwrapper = new StatisticWrapper();
            this.a.put(statistic, statisticwrapper);
        }

        statisticwrapper.a(i);
    }

    public int getStatisticValue(Statistic statistic) {
        StatisticWrapper statisticwrapper = (StatisticWrapper) this.a.get(statistic);

        return statisticwrapper == null ? 0 : statisticwrapper.a();
    }
}
