package net.minecraft.world.scores;

import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class ScoreboardObjective {

    private final Scoreboard a;
    private final String b;
    private final IScoreboardCriteria c;
    public IChatBaseComponent displayName;
    private IChatBaseComponent e;
    private IScoreboardCriteria.EnumScoreboardHealthDisplay f;

    public ScoreboardObjective(Scoreboard scoreboard, String s, IScoreboardCriteria iscoreboardcriteria, IChatBaseComponent ichatbasecomponent, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.a = scoreboard;
        this.b = s;
        this.c = iscoreboardcriteria;
        this.displayName = ichatbasecomponent;
        this.e = this.g();
        this.f = iscoreboardcriteria_enumscoreboardhealthdisplay;
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

    private IChatBaseComponent g() {
        return ChatComponentUtils.a((IChatBaseComponent) this.displayName.mutableCopy().format((chatmodifier) -> {
            return chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText(this.b)));
        }));
    }

    public IChatBaseComponent e() {
        return this.e;
    }

    public void setDisplayName(IChatBaseComponent ichatbasecomponent) {
        this.displayName = ichatbasecomponent;
        this.e = this.g();
        this.a.handleObjectiveChanged(this);
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay getRenderType() {
        return this.f;
    }

    public void setRenderType(IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.f = iscoreboardcriteria_enumscoreboardhealthdisplay;
        this.a.handleObjectiveChanged(this);
    }
}
