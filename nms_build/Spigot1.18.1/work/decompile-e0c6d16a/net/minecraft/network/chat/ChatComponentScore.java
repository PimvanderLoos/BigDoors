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

    private static final String SCORER_PLACEHOLDER = "*";
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseSelector(String s) {
        try {
            return (new ArgumentParserSelector(new StringReader(s))).parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    public ChatComponentScore(String s, String s1) {
        this(s, parseSelector(s), s1);
    }

    private ChatComponentScore(String s, @Nullable EntitySelector entityselector, String s1) {
        this.name = s;
        this.selector = entityselector;
        this.objective = s1;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public String getObjective() {
        return this.objective;
    }

    private String findTargetName(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.findEntities(commandlistenerwrapper);

            if (!list.isEmpty()) {
                if (list.size() != 1) {
                    throw ArgumentEntity.ERROR_NOT_SINGLE_ENTITY.create();
                }

                return ((Entity) list.get(0)).getScoreboardName();
            }
        }

        return this.name;
    }

    private String getScore(String s, CommandListenerWrapper commandlistenerwrapper) {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver != null) {
            ScoreboardServer scoreboardserver = minecraftserver.getScoreboard();
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(this.objective);

            if (scoreboardserver.hasPlayerScore(s, scoreboardobjective)) {
                ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);

                return Integer.toString(scoreboardscore.getScore());
            }
        }

        return "";
    }

    @Override
    public ChatComponentScore plainCopy() {
        return new ChatComponentScore(this.name, this.selector, this.objective);
    }

    @Override
    public IChatMutableComponent resolve(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper == null) {
            return new ChatComponentText("");
        } else {
            String s = this.findTargetName(commandlistenerwrapper);
            String s1 = entity != null && s.equals("*") ? entity.getScoreboardName() : s;

            return new ChatComponentText(this.getScore(s1, commandlistenerwrapper));
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

            return this.name.equals(chatcomponentscore.name) && this.objective.equals(chatcomponentscore.objective) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + "'objective='" + this.objective + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
    }
}
