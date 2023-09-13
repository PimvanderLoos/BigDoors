package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class Statistic<T> extends IScoreboardCriteria {

    private final Counter formatter;
    private final T value;
    private final StatisticWrapper<T> type;

    protected Statistic(StatisticWrapper<T> statisticwrapper, T t0, Counter counter) {
        super(buildName(statisticwrapper, t0));
        this.type = statisticwrapper;
        this.formatter = counter;
        this.value = t0;
    }

    public static <T> String buildName(StatisticWrapper<T> statisticwrapper, T t0) {
        String s = locationToKey(BuiltInRegistries.STAT_TYPE.getKey(statisticwrapper));

        return s + ":" + locationToKey(statisticwrapper.getRegistry().getKey(t0));
    }

    private static <T> String locationToKey(@Nullable MinecraftKey minecraftkey) {
        return minecraftkey.toString().replace(':', '.');
    }

    public StatisticWrapper<T> getType() {
        return this.type;
    }

    public T getValue() {
        return this.value;
    }

    public String format(int i) {
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
