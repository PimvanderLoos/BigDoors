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

    private final Scoreboard a;
    private final String b;
    private final Set<String> c = Sets.newHashSet();
    private IChatBaseComponent d;
    private IChatBaseComponent e;
    private IChatBaseComponent f;
    private boolean g;
    private boolean h;
    private ScoreboardTeamBase.EnumNameTagVisibility i;
    private ScoreboardTeamBase.EnumNameTagVisibility j;
    private EnumChatFormat k;
    private ScoreboardTeamBase.EnumTeamPush l;
    private final ChatModifier m;

    public ScoreboardTeam(Scoreboard scoreboard, String s) {
        this.e = ChatComponentText.d;
        this.f = ChatComponentText.d;
        this.g = true;
        this.h = true;
        this.i = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        this.j = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        this.k = EnumChatFormat.RESET;
        this.l = ScoreboardTeamBase.EnumTeamPush.ALWAYS;
        this.a = scoreboard;
        this.b = s;
        this.d = new ChatComponentText(s);
        this.m = ChatModifier.a.setInsertion(s).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText(s)));
    }

    @Override
    public String getName() {
        return this.b;
    }

    public IChatBaseComponent getDisplayName() {
        return this.d;
    }

    public IChatMutableComponent d() {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.a((IChatBaseComponent) this.d.mutableCopy().c(this.m));
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
            this.d = ichatbasecomponent;
            this.a.handleTeamChanged(this);
        }
    }

    public void setPrefix(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.e = ichatbasecomponent == null ? ChatComponentText.d : ichatbasecomponent;
        this.a.handleTeamChanged(this);
    }

    public IChatBaseComponent getPrefix() {
        return this.e;
    }

    public void setSuffix(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.f = ichatbasecomponent == null ? ChatComponentText.d : ichatbasecomponent;
        this.a.handleTeamChanged(this);
    }

    public IChatBaseComponent getSuffix() {
        return this.f;
    }

    @Override
    public Collection<String> getPlayerNameSet() {
        return this.c;
    }

    @Override
    public IChatMutableComponent getFormattedName(IChatBaseComponent ichatbasecomponent) {
        IChatMutableComponent ichatmutablecomponent = (new ChatComponentText("")).addSibling(this.e).addSibling(ichatbasecomponent).addSibling(this.f);
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
        return this.g;
    }

    public void setAllowFriendlyFire(boolean flag) {
        this.g = flag;
        this.a.handleTeamChanged(this);
    }

    public boolean canSeeFriendlyInvisibles() {
        return this.h;
    }

    public void setCanSeeFriendlyInvisibles(boolean flag) {
        this.h = flag;
        this.a.handleTeamChanged(this);
    }

    public ScoreboardTeamBase.EnumNameTagVisibility getNameTagVisibility() {
        return this.i;
    }

    @Override
    public ScoreboardTeamBase.EnumNameTagVisibility getDeathMessageVisibility() {
        return this.j;
    }

    public void setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility) {
        this.i = scoreboardteambase_enumnametagvisibility;
        this.a.handleTeamChanged(this);
    }

    public void setDeathMessageVisibility(ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility) {
        this.j = scoreboardteambase_enumnametagvisibility;
        this.a.handleTeamChanged(this);
    }

    @Override
    public ScoreboardTeamBase.EnumTeamPush getCollisionRule() {
        return this.l;
    }

    public void setCollisionRule(ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush) {
        this.l = scoreboardteambase_enumteampush;
        this.a.handleTeamChanged(this);
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

    public void setColor(EnumChatFormat enumchatformat) {
        this.k = enumchatformat;
        this.a.handleTeamChanged(this);
    }

    public EnumChatFormat getColor() {
        return this.k;
    }
}
