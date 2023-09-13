package net.minecraft.server;

public class ScoreboardCriteriaInteger implements IScoreboardCriteria {

    private final String o;

    public ScoreboardCriteriaInteger(String s, EnumChatFormat enumchatformat) {
        this.o = s + enumchatformat.e();
        IScoreboardCriteria.criteria.put(this.o, this);
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
