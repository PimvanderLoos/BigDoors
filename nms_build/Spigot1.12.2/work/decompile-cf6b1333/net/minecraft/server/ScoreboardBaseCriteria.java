package net.minecraft.server;

public class ScoreboardBaseCriteria implements IScoreboardCriteria {

    private final String o;

    public ScoreboardBaseCriteria(String s) {
        this.o = s;
        IScoreboardCriteria.criteria.put(s, this);
    }

    public String getName() {
        return this.o;
    }

    public boolean isReadOnly() {
        return false;
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay c() {
        return IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
    }
}
