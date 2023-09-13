package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentScoreboardObjective;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class CommandTrigger {

    private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(new ChatMessage("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(new ChatMessage("commands.trigger.failed.invalid"));

    public CommandTrigger() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("trigger").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).suggests((commandcontext, suggestionsbuilder) -> {
            return suggestObjectives((CommandListenerWrapper) commandcontext.getSource(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return simpleTrigger((CommandListenerWrapper) commandcontext.getSource(), getScore(((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException(), ArgumentScoreboardObjective.getObjective(commandcontext, "objective")));
        })).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("value", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return addValue((CommandListenerWrapper) commandcontext.getSource(), getScore(((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException(), ArgumentScoreboardObjective.getObjective(commandcontext, "objective")), IntegerArgumentType.getInteger(commandcontext, "value"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("set").then(net.minecraft.commands.CommandDispatcher.argument("value", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return setValue((CommandListenerWrapper) commandcontext.getSource(), getScore(((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException(), ArgumentScoreboardObjective.getObjective(commandcontext, "objective")), IntegerArgumentType.getInteger(commandcontext, "value"));
        })))));
    }

    public static CompletableFuture<Suggestions> suggestObjectives(CommandListenerWrapper commandlistenerwrapper, SuggestionsBuilder suggestionsbuilder) {
        Entity entity = commandlistenerwrapper.getEntity();
        List<String> list = Lists.newArrayList();

        if (entity != null) {
            ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
            String s = entity.getScoreboardName();
            Iterator iterator = scoreboardserver.getObjectives().iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                if (scoreboardobjective.getCriteria() == IScoreboardCriteria.TRIGGER && scoreboardserver.hasPlayerScore(s, scoreboardobjective)) {
                    ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);

                    if (!scoreboardscore.isLocked()) {
                        list.add(scoreboardobjective.getName());
                    }
                }
            }
        }

        return ICompletionProvider.suggest((Iterable) list, suggestionsbuilder);
    }

    private static int addValue(CommandListenerWrapper commandlistenerwrapper, ScoreboardScore scoreboardscore, int i) {
        scoreboardscore.add(i);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.trigger.add.success", new Object[]{scoreboardscore.getObjective().getFormattedDisplayName(), i}), true);
        return scoreboardscore.getScore();
    }

    private static int setValue(CommandListenerWrapper commandlistenerwrapper, ScoreboardScore scoreboardscore, int i) {
        scoreboardscore.setScore(i);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.trigger.set.success", new Object[]{scoreboardscore.getObjective().getFormattedDisplayName(), i}), true);
        return i;
    }

    private static int simpleTrigger(CommandListenerWrapper commandlistenerwrapper, ScoreboardScore scoreboardscore) {
        scoreboardscore.add(1);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.trigger.simple.success", new Object[]{scoreboardscore.getObjective().getFormattedDisplayName()}), true);
        return scoreboardscore.getScore();
    }

    private static ScoreboardScore getScore(EntityPlayer entityplayer, ScoreboardObjective scoreboardobjective) throws CommandSyntaxException {
        if (scoreboardobjective.getCriteria() != IScoreboardCriteria.TRIGGER) {
            throw CommandTrigger.ERROR_INVALID_OBJECTIVE.create();
        } else {
            Scoreboard scoreboard = entityplayer.getScoreboard();
            String s = entityplayer.getScoreboardName();

            if (!scoreboard.hasPlayerScore(s, scoreboardobjective)) {
                throw CommandTrigger.ERROR_NOT_PRIMED.create();
            } else {
                ScoreboardScore scoreboardscore = scoreboard.getOrCreatePlayerScore(s, scoreboardobjective);

                if (scoreboardscore.isLocked()) {
                    throw CommandTrigger.ERROR_NOT_PRIMED.create();
                } else {
                    scoreboardscore.setLocked(true);
                    return scoreboardscore;
                }
            }
        }
    }
}
