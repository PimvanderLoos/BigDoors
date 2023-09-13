package net.minecraft.server;

public class ScoreboardReadOnlyCriteria extends ScoreboardBaseCriteria {

    public ScoreboardReadOnlyCriteria(String s) {
        super(s);
    }

    public boolean isReadOnly() {
        return true;
    }
}
