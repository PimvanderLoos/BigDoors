package net.minecraft.world.scores;

import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class ScoreboardObjective {

    public static final int MAX_NAME_LENGTH = 16;
    private final Scoreboard scoreboard;
    private final String name;
    private final IScoreboardCriteria criteria;
    public IChatBaseComponent displayName;
    private IChatBaseComponent formattedDisplayName;
    private IScoreboardCriteria.EnumScoreboardHealthDisplay renderType;

    public ScoreboardObjective(Scoreboard scoreboard, String s, IScoreboardCriteria iscoreboardcriteria, IChatBaseComponent ichatbasecomponent, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.scoreboard = scoreboard;
        this.name = s;
        this.criteria = iscoreboardcriteria;
        this.displayName = ichatbasecomponent;
        this.formattedDisplayName = this.g();
        this.renderType = iscoreboardcriteria_enumscoreboardhealthdisplay;
    }

    public Scoreboard a() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public IScoreboardCriteria getCriteria() {
        return this.criteria;
    }

    public IChatBaseComponent getDisplayName() {
        return this.displayName;
    }

    private IChatBaseComponent g() {
        return ChatComponentUtils.a((IChatBaseComponent) this.displayName.mutableCopy().format((chatmodifier) -> {
            return chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText(this.name)));
        }));
    }

    public IChatBaseComponent e() {
        return this.formattedDisplayName;
    }

    public void setDisplayName(IChatBaseComponent ichatbasecomponent) {
        this.displayName = ichatbasecomponent;
        this.formattedDisplayName = this.g();
        this.scoreboard.handleObjectiveChanged(this);
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay getRenderType() {
        return this.renderType;
    }

    public void setRenderType(IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.renderType = iscoreboardcriteria_enumscoreboardhealthdisplay;
        this.scoreboard.handleObjectiveChanged(this);
    }
}
