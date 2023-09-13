package net.minecraft.world.scores;

import java.util.Comparator;
import javax.annotation.Nullable;

public class ScoreboardScore {

    public static final Comparator<ScoreboardScore> SCORE_COMPARATOR = (scoreboardscore, scoreboardscore1) -> {
        return scoreboardscore.getScore() > scoreboardscore1.getScore() ? 1 : (scoreboardscore.getScore() < scoreboardscore1.getScore() ? -1 : scoreboardscore1.getPlayerName().compareToIgnoreCase(scoreboardscore.getPlayerName()));
    };
    private final Scoreboard scoreboard;
    @Nullable
    private final ScoreboardObjective objective;
    private final String owner;
    private int count;
    private boolean locked;
    private boolean forceUpdate;

    public ScoreboardScore(Scoreboard scoreboard, ScoreboardObjective scoreboardobjective, String s) {
        this.scoreboard = scoreboard;
        this.objective = scoreboardobjective;
        this.owner = s;
        this.locked = true;
        this.forceUpdate = true;
    }

    public void addScore(int i) {
        if (this.objective.getCriteria().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        } else {
            this.setScore(this.getScore() + i);
        }
    }

    public void incrementScore() {
        this.addScore(1);
    }

    public int getScore() {
        return this.count;
    }

    public void c() {
        this.setScore(0);
    }

    public void setScore(int i) {
        int j = this.count;

        this.count = i;
        if (j != i || this.forceUpdate) {
            this.forceUpdate = false;
            this.f().handleScoreChanged(this);
        }

    }

    @Nullable
    public ScoreboardObjective getObjective() {
        return this.objective;
    }

    public String getPlayerName() {
        return this.owner;
    }

    public Scoreboard f() {
        return this.scoreboard;
    }

    public boolean g() {
        return this.locked;
    }

    public void a(boolean flag) {
        this.locked = flag;
    }
}
