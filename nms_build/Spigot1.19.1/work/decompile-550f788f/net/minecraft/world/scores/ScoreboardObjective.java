package net.minecraft.world.scores;

import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class ScoreboardObjective {

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
        this.formattedDisplayName = this.createFormattedDisplayName();
        this.renderType = iscoreboardcriteria_enumscoreboardhealthdisplay;
    }

    public Scoreboard getScoreboard() {
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

    private IChatBaseComponent createFormattedDisplayName() {
        return ChatComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle((chatmodifier) -> {
            return chatmodifier.withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.literal(this.name)));
        }));
    }

    public IChatBaseComponent getFormattedDisplayName() {
        return this.formattedDisplayName;
    }

    public void setDisplayName(IChatBaseComponent ichatbasecomponent) {
        this.displayName = ichatbasecomponent;
        this.formattedDisplayName = this.createFormattedDisplayName();
        this.scoreboard.onObjectiveChanged(this);
    }

    public IScoreboardCriteria.EnumScoreboardHealthDisplay getRenderType() {
        return this.renderType;
    }

    public void setRenderType(IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        this.renderType = iscoreboardcriteria_enumscoreboardhealthdisplay;
        this.scoreboard.onObjectiveChanged(this);
    }
}
