package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.selector.ArgumentParserSelector;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;

public class ScoreContents implements ComponentContents {

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

    public ScoreContents(String s, String s1) {
        this.name = s;
        this.selector = parseSelector(s);
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
    public IChatMutableComponent resolve(@Nullable CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, int i) throws CommandSyntaxException {
        if (commandlistenerwrapper == null) {
            return IChatBaseComponent.empty();
        } else {
            String s = this.findTargetName(commandlistenerwrapper);
            String s1 = entity != null && s.equals("*") ? entity.getScoreboardName() : s;

            return IChatBaseComponent.literal(this.getScore(s1, commandlistenerwrapper));
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            boolean flag;

            if (object instanceof ScoreContents) {
                ScoreContents scorecontents = (ScoreContents) object;

                if (this.name.equals(scorecontents.name) && this.objective.equals(scorecontents.objective)) {
                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode() {
        int i = this.name.hashCode();

        i = 31 * i + this.objective.hashCode();
        return i;
    }

    public String toString() {
        return "score{name='" + this.name + "', objective='" + this.objective + "'}";
    }
}
