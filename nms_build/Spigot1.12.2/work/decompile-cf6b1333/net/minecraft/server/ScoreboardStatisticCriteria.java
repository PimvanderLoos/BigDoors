package net.minecraft.server;

public class ScoreboardStatisticCriteria extends ScoreboardBaseCriteria {

    private final Statistic o;

    public ScoreboardStatisticCriteria(Statistic statistic) {
        super(statistic.name);
        this.o = statistic;
    }
}
