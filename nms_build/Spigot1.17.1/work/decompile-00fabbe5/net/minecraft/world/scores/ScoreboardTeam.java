package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class ScoreboardTeam extends ScoreboardTeamBase {

    public static final int MAX_NAME_LENGTH = 16;
    private static final int BIT_FRIENDLY_FIRE = 0;
    private static final int BIT_SEE_INVISIBLES = 1;
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> players = Sets.newHashSet();
    private IChatBaseComponent displayName;
    private IChatBaseComponent playerPrefix;
    private IChatBaseComponent playerSuffix;
    private boolean allowFriendlyFire;
    private boolean seeFriendlyInvisibles;
    private ScoreboardTeamBase.EnumNameTagVisibility nameTagVisibility;
    private ScoreboardTeamBase.EnumNameTagVisibility deathMessageVisibility;
    private EnumChatFormat color;
    private ScoreboardTeamBase.EnumTeamPush collisionRule;
    private final ChatModifier displayNameStyle;

    public ScoreboardTeam(Scoreboard scoreboard, String s) {
        this.playerPrefix = ChatComponentText.EMPTY;
        this.playerSuffix = ChatComponentText.EMPTY;
        this.allowFriendlyFire = true;
        this.seeFriendlyInvisibles = true;
        this.nameTagVisibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        this.deathMessageVisibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        this.color = EnumChatFormat.RESET;
        this.collisionRule = ScoreboardTeamBase.EnumTeamPush.ALWAYS;
        this.scoreboard = scoreboard;
        this.name = s;
        this.displayName = new ChatComponentText(s);
        this.displayNameStyle = ChatModifier.EMPTY.setInsertion(s).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText(s)));
    }

    public Scoreboard a() {
        return this.scoreboard;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IChatBaseComponent getDisplayName() {
        return this.displayName;
    }

    public IChatMutableComponent d() {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.a((IChatBaseComponent) this.displayName.mutableCopy().c(this.displayNameStyle));
        EnumChatFormat enumchatformat = this.getColor();

        if (enumchatformat != EnumChatFormat.RESET) {
            ichatmutablecomponent.a(enumchatformat);
        }

        return ichatmutablecomponent;
    }

    public void setDisplayName(IChatBaseComponent ichatbasecomponent) {
        if (ichatbasecomponent == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.displayName = ichatbasecomponent;
            this.scoreboard.handleTeamChanged(this);
        }
    }

    public void setPrefix(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.playerPrefix = ichatbasecomponent == null ? ChatComponentText.EMPTY : ichatbasecomponent;
        this.scoreboard.handleTeamChanged(this);
    }

    public IChatBaseComponent getPrefix() {
        return this.playerPrefix;
    }

    public void setSuffix(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.playerSuffix = ichatbasecomponent == null ? ChatComponentText.EMPTY : ichatbasecomponent;
        this.scoreboard.handleTeamChanged(this);
    }

    public IChatBaseComponent getSuffix() {
        return this.playerSuffix;
    }

    @Override
    public Collection<String> getPlayerNameSet() {
        return this.players;
    }

    @Override
    public IChatMutableComponent getFormattedName(IChatBaseComponent ichatbasecomponent) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("")).addSibling(this.playerPrefix).addSibling(ichatbasecomponent).addSibling(this.playerSuffix);
        EnumChatFormat enumchatformat = this.getColor();

        if (enumchatformat != EnumChatFormat.RESET) {
            ichatmutablecomponent.a(enumchatformat);
        }

        return ichatmutablecomponent;
    }

    public static IChatMutableComponent a(@Nullable ScoreboardTeamBase scoreboardteambase, IChatBaseComponent ichatbasecomponent) {
        return scoreboardteambase == null ? ichatbasecomponent.mutableCopy() : scoreboardteambase.getFormattedName(ichatbasecomponent);
    }

    @Override
    public boolean allowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean flag) {
        this.allowFriendlyFire = flag;
        this.scoreboard.handleTeamChanged(this);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return this.seeFriendlyInvisibles;
    }

    public void setCanSeeFriendlyInvisibles(boolean flag) {
        this.seeFriendlyInvisibles = flag;
        this.scoreboard.handleTeamChanged(this);
    }

    @Override
    public ScoreboardTeamBase.EnumNameTagVisibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    @Override
    public ScoreboardTeamBase.EnumNameTagVisibility getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility) {
        this.nameTagVisibility = scoreboardteambase_enumnametagvisibility;
        this.scoreboard.handleTeamChanged(this);
    }

    public void setDeathMessageVisibility(ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility) {
        this.deathMessageVisibility = scoreboardteambase_enumnametagvisibility;
        this.scoreboard.handleTeamChanged(this);
    }

    @Override
    public ScoreboardTeamBase.EnumTeamPush getCollisionRule() {
        return this.collisionRule;
    }

    public void setCollisionRule(ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush) {
        this.collisionRule = scoreboardteambase_enumteampush;
        this.scoreboard.handleTeamChanged(this);
    }

    public int packOptionData() {
        int i = 0;

        if (this.allowFriendlyFire()) {
            i |= 1;
        }

        if (this.canSeeFriendlyInvisibles()) {
            i |= 2;
        }

        return i;
    }

    public void a(int i) {
        this.setAllowFriendlyFire((i & 1) > 0);
        this.setCanSeeFriendlyInvisibles((i & 2) > 0);
    }

    public void setColor(EnumChatFormat enumchatformat) {
        this.color = enumchatformat;
        this.scoreboard.handleTeamChanged(this);
    }

    @Override
    public EnumChatFormat getColor() {
        return this.color;
    }
}
