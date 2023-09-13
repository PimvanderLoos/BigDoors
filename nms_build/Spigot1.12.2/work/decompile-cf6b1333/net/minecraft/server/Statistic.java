package net.minecraft.server;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Statistic {

    public final String name;
    private final IChatBaseComponent g;
    public boolean b;
    private final Counter h;
    private final IScoreboardCriteria i;
    private Class<? extends IJsonStatistic> j;
    private static final NumberFormat k = NumberFormat.getIntegerInstance(Locale.US);
    public static Counter c = new Counter() {
    };
    private static final DecimalFormat l = new DecimalFormat("########0.00");
    public static Counter d = new Counter() {
    };
    public static Counter e = new Counter() {
    };
    public static Counter f = new Counter() {
    };

    public Statistic(String s, IChatBaseComponent ichatbasecomponent, Counter counter) {
        this.name = s;
        this.g = ichatbasecomponent;
        this.h = counter;
        this.i = new ScoreboardStatisticCriteria(this);
        IScoreboardCriteria.criteria.put(this.i.getName(), this.i);
    }

    public Statistic(String s, IChatBaseComponent ichatbasecomponent) {
        this(s, ichatbasecomponent, Statistic.c);
    }

    public Statistic c() {
        this.b = true;
        return this;
    }

    public Statistic a() {
        if (StatisticList.a.containsKey(this.name)) {
            throw new RuntimeException("Duplicate stat id: \"" + ((Statistic) StatisticList.a.get(this.name)).g + "\" and \"" + this.g + "\" at id " + this.name);
        } else {
            StatisticList.stats.add(this);
            StatisticList.a.put(this.name, this);
            return this;
        }
    }

    public IChatBaseComponent d() {
        IChatBaseComponent ichatbasecomponent = this.g.f();

        ichatbasecomponent.getChatModifier().setColor(EnumChatFormat.GRAY);
        return ichatbasecomponent;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            Statistic statistic = (Statistic) object;

            return this.name.equals(statistic.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return "Stat{id=" + this.name + ", nameId=" + this.g + ", awardLocallyOnly=" + this.b + ", formatter=" + this.h + ", objectiveCriteria=" + this.i + '}';
    }

    public IScoreboardCriteria f() {
        return this.i;
    }

    public Class<? extends IJsonStatistic> g() {
        return this.j;
    }
}
