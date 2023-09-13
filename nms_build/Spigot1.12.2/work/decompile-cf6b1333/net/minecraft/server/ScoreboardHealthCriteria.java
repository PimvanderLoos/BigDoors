package net.minecraft.server;

public class ScoreboardHealthCriteria extends ScoreboardBaseCriteria {

    public ScoreboardHealthCriteria(String s) {
        super(s);
    }

    public boolean isReadOnly() {
        return true;
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay c() {
        return IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS;
    }
}
