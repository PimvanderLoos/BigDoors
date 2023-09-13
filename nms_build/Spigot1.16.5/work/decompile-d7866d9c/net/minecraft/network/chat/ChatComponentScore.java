package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;

public class ChatComponentScore extends ChatBaseComponent implements ChatComponentContextual {

    private final String d;
    @Nullable
    private final EntitySelector e;
    private final String f;

    @Nullable
    private static EntitySelector d(String s) {
        try {
            return (new ArgumentParserSelector(new StringReader(s))).parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    public ChatComponentScore(String s, String s1) {
        this(s, d(s), s1);
    }

    private ChatComponentScore(String s, @Nullable EntitySelector entityselector, String s1) {
        this.d = s;
        this.e = entityselector;
        this.f = s1;
    }

    public String h() {
        return this.d;
    }

    public String j() {
        return this.f;
    }

    private String a(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        if (this.e != null) {
            List<? extends Entity> list = this.e.getEntities(commandlistenerwrapper);

            if (!list.isEmpty()) {
                if (list.size() != 1) {
                    throw ArgumentEntity.a.create();
                }

                return ((Entity) list.get(0)).getName();
            }
        }

        return this.d;
    }

    private String a(String s, CommandListenerWrapper commandlistenerwrapper) {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver != null) {
            ScoreboardServer scoreboardserver = minecraftserver.getScoreboard();
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(this.f);

            if (scoreboardserver.b(s, scoreboardobjective)) {
                ScoreboardScore scoreboardscore = scoreboardserver.getPlayerScoreForObjective(s, scoreboardobjective);

                return Integer.toString(scoreboardscore.getScore());
            }
        }

        return "";
    }

    @Override
    public ChatComponentScore g() {
        return new ChatComponentScore(this.d, this.e, this.f);
    }

    @Override
    public IChatMutableComponent a(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper == null) {
            return new ChatComponentText("");
        } else {
            String s = this.a(commandlistenerwrapper);
            String s1 = entity != null && s.equals("*") ? entity.getName() : s;

            return new ChatComponentText(this.a(s1, commandlistenerwrapper));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChatComponentScore)) {
            return false;
        } else {
            ChatComponentScore chatcomponentscore = (ChatComponentScore) object;

            return this.d.equals(chatcomponentscore.d) && this.f.equals(chatcomponentscore.f) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.d + '\'' + "objective='" + this.f + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatModifier() + '}';
    }
}
