package net.minecraft.world.scores;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public abstract class ScoreboardTeamBase {

    public ScoreboardTeamBase() {}

    public boolean isAlly(@Nullable ScoreboardTeamBase scoreboardteambase) {
        return scoreboardteambase == null ? false : this == scoreboardteambase;
    }

    public abstract String getName();

    public abstract IChatMutableComponent getFormattedName(IChatBaseComponent ichatbasecomponent);

    public abstract boolean canSeeFriendlyInvisibles();

    public abstract boolean allowFriendlyFire();

    public abstract ScoreboardTeamBase.EnumNameTagVisibility getNameTagVisibility();

    public abstract EnumChatFormat getColor();

    public abstract Collection<String> getPlayerNameSet();

    public abstract ScoreboardTeamBase.EnumNameTagVisibility getDeathMessageVisibility();

    public abstract ScoreboardTeamBase.EnumTeamPush getCollisionRule();

    public static enum EnumTeamPush {

        ALWAYS("always", 0), NEVER("never", 1), PUSH_OTHER_TEAMS("pushOtherTeams", 2), PUSH_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, ScoreboardTeamBase.EnumTeamPush> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap((scoreboardteambase_enumteampush) -> {
            return scoreboardteambase_enumteampush.name;
        }, (scoreboardteambase_enumteampush) -> {
            return scoreboardteambase_enumteampush;
        }));
        public final String name;
        public final int id;

        @Nullable
        public static ScoreboardTeamBase.EnumTeamPush a(String s) {
            return (ScoreboardTeamBase.EnumTeamPush) ScoreboardTeamBase.EnumTeamPush.BY_NAME.get(s);
        }

        private EnumTeamPush(String s, int i) {
            this.name = s;
            this.id = i;
        }

        public IChatBaseComponent a() {
            return new ChatMessage("team.collision." + this.name);
        }
    }

    public static enum EnumNameTagVisibility {

        ALWAYS("always", 0), NEVER("never", 1), HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2), HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, ScoreboardTeamBase.EnumNameTagVisibility> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap((scoreboardteambase_enumnametagvisibility) -> {
            return scoreboardteambase_enumnametagvisibility.name;
        }, (scoreboardteambase_enumnametagvisibility) -> {
            return scoreboardteambase_enumnametagvisibility;
        }));
        public final String name;
        public final int id;

        public static String[] a() {
            return (String[]) ScoreboardTeamBase.EnumNameTagVisibility.BY_NAME.keySet().toArray(new String[ScoreboardTeamBase.EnumNameTagVisibility.BY_NAME.size()]);
        }

        @Nullable
        public static ScoreboardTeamBase.EnumNameTagVisibility a(String s) {
            return (ScoreboardTeamBase.EnumNameTagVisibility) ScoreboardTeamBase.EnumNameTagVisibility.BY_NAME.get(s);
        }

        private EnumNameTagVisibility(String s, int i) {
            this.name = s;
            this.id = i;
        }

        public IChatBaseComponent b() {
            return new ChatMessage("team.visibility." + this.name);
        }
    }
}
