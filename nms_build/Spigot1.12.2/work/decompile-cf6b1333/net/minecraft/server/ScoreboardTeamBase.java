package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

public abstract class ScoreboardTeamBase {

    public ScoreboardTeamBase() {}

    public boolean isAlly(@Nullable ScoreboardTeamBase scoreboardteambase) {
        return scoreboardteambase == null ? false : this == scoreboardteambase;
    }

    public abstract String getName();

    public abstract String getFormattedName(String s);

    public abstract boolean allowFriendlyFire();

    public abstract EnumChatFormat getColor();

    public abstract Collection<String> getPlayerNameSet();

    public abstract ScoreboardTeamBase.EnumNameTagVisibility getDeathMessageVisibility();

    public abstract ScoreboardTeamBase.EnumTeamPush getCollisionRule();

    public static enum EnumTeamPush {

        ALWAYS("always", 0), NEVER("never", 1), HIDE_FOR_OTHER_TEAMS("pushOtherTeams", 2), HIDE_FOR_OWN_TEAM("pushOwnTeam", 3);

        private static final Map<String, ScoreboardTeamBase.EnumTeamPush> g = Maps.newHashMap();
        public final String e;
        public final int f;

        public static String[] a() {
            return (String[]) ScoreboardTeamBase.EnumTeamPush.g.keySet().toArray(new String[ScoreboardTeamBase.EnumTeamPush.g.size()]);
        }

        @Nullable
        public static ScoreboardTeamBase.EnumTeamPush a(String s) {
            return (ScoreboardTeamBase.EnumTeamPush) ScoreboardTeamBase.EnumTeamPush.g.get(s);
        }

        private EnumTeamPush(String s, int i) {
            this.e = s;
            this.f = i;
        }

        static {
            ScoreboardTeamBase.EnumTeamPush[] ascoreboardteambase_enumteampush = values();
            int i = ascoreboardteambase_enumteampush.length;

            for (int j = 0; j < i; ++j) {
                ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush = ascoreboardteambase_enumteampush[j];

                ScoreboardTeamBase.EnumTeamPush.g.put(scoreboardteambase_enumteampush.e, scoreboardteambase_enumteampush);
            }

        }
    }

    public static enum EnumNameTagVisibility {

        ALWAYS("always", 0), NEVER("never", 1), HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2), HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

        private static final Map<String, ScoreboardTeamBase.EnumNameTagVisibility> g = Maps.newHashMap();
        public final String e;
        public final int f;

        public static String[] a() {
            return (String[]) ScoreboardTeamBase.EnumNameTagVisibility.g.keySet().toArray(new String[ScoreboardTeamBase.EnumNameTagVisibility.g.size()]);
        }

        @Nullable
        public static ScoreboardTeamBase.EnumNameTagVisibility a(String s) {
            return (ScoreboardTeamBase.EnumNameTagVisibility) ScoreboardTeamBase.EnumNameTagVisibility.g.get(s);
        }

        private EnumNameTagVisibility(String s, int i) {
            this.e = s;
            this.f = i;
        }

        static {
            ScoreboardTeamBase.EnumNameTagVisibility[] ascoreboardteambase_enumnametagvisibility = values();
            int i = ascoreboardteambase_enumnametagvisibility.length;

            for (int j = 0; j < i; ++j) {
                ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility = ascoreboardteambase_enumnametagvisibility[j];

                ScoreboardTeamBase.EnumNameTagVisibility.g.put(scoreboardteambase_enumnametagvisibility.e, scoreboardteambase_enumnametagvisibility);
            }

        }
    }
}
