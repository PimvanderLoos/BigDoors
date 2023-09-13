package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;

public class ChatComponentScore extends ChatBaseComponent {

    private final String b;
    @Nullable
    private final EntitySelector c;
    private final String d;
    private String e = "";

    public ChatComponentScore(String s, String s1) {
        this.b = s;
        this.d = s1;
        EntitySelector entityselector = null;

        try {
            ArgumentParserSelector argumentparserselector = new ArgumentParserSelector(new StringReader(s));

            entityselector = argumentparserselector.s();
        } catch (CommandSyntaxException commandsyntaxexception) {
            ;
        }

        this.c = entityselector;
    }

    public String i() {
        return this.b;
    }

    @Nullable
    public EntitySelector j() {
        return this.c;
    }

    public String k() {
        return this.d;
    }

    public void b(String s) {
        this.e = s;
    }

    public String getText() {
        return this.e;
    }

    public void b(CommandListenerWrapper commandlistenerwrapper) {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver != null && minecraftserver.D() && UtilColor.b(this.e)) {
            ScoreboardServer scoreboardserver = minecraftserver.getScoreboard();
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(this.d);

            if (scoreboardserver.b(this.b, scoreboardobjective)) {
                ScoreboardScore scoreboardscore = scoreboardserver.getPlayerScoreForObjective(this.b, scoreboardobjective);

                this.b(String.format("%d", scoreboardscore.getScore()));
            } else {
                this.e = "";
            }
        }

    }

    public ChatComponentScore g() {
        ChatComponentScore chatcomponentscore = new ChatComponentScore(this.b, this.d);

        chatcomponentscore.b(this.e);
        return chatcomponentscore;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentScore)) {
            return false;
        } else {
            ChatComponentScore chatcomponentscore = (ChatComponentScore) object;

            return this.b.equals(chatcomponentscore.b) && this.d.equals(chatcomponentscore.d) && super.equals(object);
        }
    }

    public String toString() {
        return "ScoreComponent{name='" + this.b + '\'' + "objective='" + this.d + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }
}
