package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class Statistic<T> extends IScoreboardCriteria {

    private final Counter formatter;
    private final T value;
    private final StatisticWrapper<T> type;

    protected Statistic(StatisticWrapper<T> statisticwrapper, T t0, Counter counter) {
        super(a(statisticwrapper, t0));
        this.type = statisticwrapper;
        this.formatter = counter;
        this.value = t0;
    }

    public static <T> String a(StatisticWrapper<T> statisticwrapper, T t0) {
        String s = a(IRegistry.STAT_TYPE.getKey(statisticwrapper));

        return s + ":" + a(statisticwrapper.getRegistry().getKey(t0));
    }

    private static <T> String a(@Nullable MinecraftKey minecraftkey) {
        return minecraftkey.toString().replace(':', '.');
    }

    public StatisticWrapper<T> getWrapper() {
        return this.type;
    }

    public T b() {
        return this.value;
    }

    public String a(int i) {
        return this.formatter.format(i);
    }

    public boolean equals(Object object) {
        return this == object || object instanceof Statistic && Objects.equals(this.getName(), ((Statistic) object).getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public String toString() {
        String s = this.getName();

        return "Stat{name=" + s + ", formatter=" + this.formatter + "}";
    }
}
