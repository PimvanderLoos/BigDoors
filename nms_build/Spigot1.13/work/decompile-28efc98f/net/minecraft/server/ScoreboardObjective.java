package net.minecraft.server;

import java.util.function.Consumer;

public class ScoreboardObjective {

    private final Scoreboard a;
    private final String b;
    private final IScoreboardCriteria c;
    public IChatBaseComponent displayName;
    private IScoreboardCriteria.EnumScoreboardHealthDisplay e;

    public ScoreboardObjective(Scoreboard scoreboard, String s, IScoreboardCriteria iscoreboardcriteria, IChatBaseComponent ichatbasecomponent, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.a = scoreboard;
        this.b = s;
        this.c = iscoreboardcriteria;
        this.displayName = ichatbasecomponent;
        this.e = iscoreboardcriteria_enumscoreboardhealthdisplay;
    }

    public String getName() {
        return this.b;
    }

    public IScoreboardCriteria getCriteria() {
        return this.c;
    }

    public IChatBaseComponent getDisplayName() {
        return this.displayName;
    }

    public IChatBaseComponent e() {
        return ChatComponentUtils.a(this.displayName.e().a((chatmodifier) -> {
            chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText(this.getName())));
        }));
    }

    public void setDisplayName(IChatBaseComponent ichatbasecomponent) {
        this.displayName = ichatbasecomponent;
        this.a.handleObjectiveChanged(this);
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay f() {
        return this.e;
    }

    public void a(IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.e = iscoreboardcriteria_enumscoreboardhealthdisplay;
        this.a.handleObjectiveChanged(this);
    }
}
