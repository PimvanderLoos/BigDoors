package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentMathOperation;
import net.minecraft.commands.arguments.ArgumentScoreboardCriteria;
import net.minecraft.commands.arguments.ArgumentScoreboardObjective;
import net.minecraft.commands.arguments.ArgumentScoreboardSlot;
import net.minecraft.commands.arguments.ArgumentScoreholder;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardObjective;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class CommandScoreboard {

    private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType(new ChatMessage("commands.scoreboard.objectives.add.duplicate"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType(new ChatMessage("commands.scoreboard.objectives.display.alreadyEmpty"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType(new ChatMessage("commands.scoreboard.objectives.display.alreadySet"));
    private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType(new ChatMessage("commands.scoreboard.players.enable.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType(new ChatMessage("commands.scoreboard.players.enable.invalid"));
    private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.scoreboard.players.get.null", new Object[]{object, object1});
    });

    public CommandScoreboard() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("scoreboard").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("objectives").then(net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listObjectives((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("criteria", ArgumentScoreboardCriteria.criteria()).executes((commandcontext) -> {
            return addObjective((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "objective"), ArgumentScoreboardCriteria.getCriteria(commandcontext, "criteria"), new ChatComponentText(StringArgumentType.getString(commandcontext, "objective")));
        })).then(net.minecraft.commands.CommandDispatcher.argument("displayName", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return addObjective((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "objective"), ArgumentScoreboardCriteria.getCriteria(commandcontext, "criteria"), ArgumentChatComponent.getComponent(commandcontext, "displayName"));
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("modify").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).then(net.minecraft.commands.CommandDispatcher.literal("displayname").then(net.minecraft.commands.CommandDispatcher.argument("displayName", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return setDisplayName((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"), ArgumentChatComponent.getComponent(commandcontext, "displayName"));
        })))).then(createRenderTypeModify())))).then(net.minecraft.commands.CommandDispatcher.literal("remove").then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).executes((commandcontext) -> {
            return removeObjective((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("setdisplay").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("slot", ArgumentScoreboardSlot.displaySlot()).executes((commandcontext) -> {
            return clearDisplaySlot((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardSlot.getDisplaySlot(commandcontext, "slot"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).executes((commandcontext) -> {
            return setDisplaySlot((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardSlot.getDisplaySlot(commandcontext, "slot"), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"));
        })))))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("players").then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listTrackedPlayers((CommandListenerWrapper) commandcontext.getSource());
        })).then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).executes((commandcontext) -> {
            return listTrackedPlayerScores((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getName(commandcontext, "target"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("set").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).then(net.minecraft.commands.CommandDispatcher.argument("score", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return setScore((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getWritableObjective(commandcontext, "objective"), IntegerArgumentType.getInteger(commandcontext, "score"));
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("get").then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentScoreholder.scoreHolder()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).executes((commandcontext) -> {
            return getScore((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getName(commandcontext, "target"), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("add").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).then(net.minecraft.commands.CommandDispatcher.argument("score", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return addScore((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getWritableObjective(commandcontext, "objective"), IntegerArgumentType.getInteger(commandcontext, "score"));
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("remove").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).then(net.minecraft.commands.CommandDispatcher.argument("score", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return removeScore((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getWritableObjective(commandcontext, "objective"), IntegerArgumentType.getInteger(commandcontext, "score"));
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("reset").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).executes((commandcontext) -> {
            return resetScores((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).executes((commandcontext) -> {
            return resetScore((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("enable").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("objective", ArgumentScoreboardObjective.objective()).suggests((commandcontext, suggestionsbuilder) -> {
            return suggestTriggers((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return enableTrigger((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("operation").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("targetObjective", ArgumentScoreboardObjective.objective()).then(net.minecraft.commands.CommandDispatcher.argument("operation", ArgumentMathOperation.operation()).then(net.minecraft.commands.CommandDispatcher.argument("source", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).then(net.minecraft.commands.CommandDispatcher.argument("sourceObjective", ArgumentScoreboardObjective.objective()).executes((commandcontext) -> {
            return performOperation((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "targets"), ArgumentScoreboardObjective.getWritableObjective(commandcontext, "targetObjective"), ArgumentMathOperation.getOperation(commandcontext, "operation"), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "source"), ArgumentScoreboardObjective.getObjective(commandcontext, "sourceObjective"));
        })))))))));
    }

    private static LiteralArgumentBuilder<CommandListenerWrapper> createRenderTypeModify() {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = net.minecraft.commands.CommandDispatcher.literal("rendertype");
        IScoreboardCriteria.EnumScoreboardHealthDisplay[] aiscoreboardcriteria_enumscoreboardhealthdisplay = IScoreboardCriteria.EnumScoreboardHealthDisplay.values();
        int i = aiscoreboardcriteria_enumscoreboardhealthdisplay.length;

        for (int j = 0; j < i; ++j) {
            IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay = aiscoreboardcriteria_enumscoreboardhealthdisplay[j];

            literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal(iscoreboardcriteria_enumscoreboardhealthdisplay.getId()).executes((commandcontext) -> {
                return setRenderType((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardObjective.getObjective(commandcontext, "objective"), iscoreboardcriteria_enumscoreboardhealthdisplay);
            }));
        }

        return literalargumentbuilder;
    }

    private static CompletableFuture<Suggestions> suggestTriggers(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, SuggestionsBuilder suggestionsbuilder) {
        List<String> list = Lists.newArrayList();
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Iterator iterator = scoreboardserver.getObjectives().iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

            if (scoreboardobjective.getCriteria() == IScoreboardCriteria.TRIGGER) {
                boolean flag = false;
                Iterator iterator1 = collection.iterator();

                while (true) {
                    if (iterator1.hasNext()) {
                        String s = (String) iterator1.next();

                        if (scoreboardserver.hasPlayerScore(s, scoreboardobjective) && !scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective).isLocked()) {
                            continue;
                        }

                        flag = true;
                    }

                    if (flag) {
                        list.add(scoreboardobjective.getName());
                    }
                    break;
                }
            }
        }

        return ICompletionProvider.suggest((Iterable) list, suggestionsbuilder);
    }

    private static int getScore(CommandListenerWrapper commandlistenerwrapper, String s, ScoreboardObjective scoreboardobjective) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        if (!scoreboardserver.hasPlayerScore(s, scoreboardobjective)) {
            throw CommandScoreboard.ERROR_NO_VALUE.create(scoreboardobjective.getName(), s);
        } else {
            ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.get.success", new Object[]{s, scoreboardscore.getScore(), scoreboardobjective.getFormattedDisplayName()}), false);
            return scoreboardscore.getScore();
        }
    }

    private static int performOperation(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective, ArgumentMathOperation.a argumentmathoperation_a, Collection<String> collection1, ScoreboardObjective scoreboardobjective1) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        int i = 0;

        ScoreboardScore scoreboardscore;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += scoreboardscore.getScore()) {
            String s = (String) iterator.next();

            scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);
            Iterator iterator1 = collection1.iterator();

            while (iterator1.hasNext()) {
                String s1 = (String) iterator1.next();
                ScoreboardScore scoreboardscore1 = scoreboardserver.getOrCreatePlayerScore(s1, scoreboardobjective1);

                argumentmathoperation_a.apply(scoreboardscore, scoreboardscore1);
            }
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.operation.success.single", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.iterator().next(), i}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.operation.success.multiple", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.size()}), true);
        }

        return i;
    }

    private static int enableTrigger(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective) throws CommandSyntaxException {
        if (scoreboardobjective.getCriteria() != IScoreboardCriteria.TRIGGER) {
            throw CommandScoreboard.ERROR_NOT_TRIGGER.create();
        } else {
            ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
            int i = 0;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);

                if (scoreboardscore.isLocked()) {
                    scoreboardscore.setLocked(false);
                    ++i;
                }
            }

            if (i == 0) {
                throw CommandScoreboard.ERROR_TRIGGER_ALREADY_ENABLED.create();
            } else {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.enable.success.single", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.iterator().next()}), true);
                } else {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.enable.success.multiple", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.size()}), true);
                }

                return i;
            }
        }
    }

    private static int resetScores(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            scoreboardserver.resetPlayerScore(s, (ScoreboardObjective) null);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.reset.all.single", new Object[]{collection.iterator().next()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.reset.all.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int resetScore(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            scoreboardserver.resetPlayerScore(s, scoreboardobjective);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.reset.specific.single", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.iterator().next()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.reset.specific.multiple", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.size()}), true);
        }

        return collection.size();
    }

    private static int setScore(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective, int i) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            ScoreboardScore scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);

            scoreboardscore.setScore(i);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.set.success.single", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.iterator().next(), i}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.set.success.multiple", new Object[]{scoreboardobjective.getFormattedDisplayName(), collection.size(), i}), true);
        }

        return i * collection.size();
    }

    private static int addScore(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective, int i) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        int j = 0;

        ScoreboardScore scoreboardscore;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); j += scoreboardscore.getScore()) {
            String s = (String) iterator.next();

            scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);
            scoreboardscore.setScore(scoreboardscore.getScore() + i);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.add.success.single", new Object[]{i, scoreboardobjective.getFormattedDisplayName(), collection.iterator().next(), j}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.add.success.multiple", new Object[]{i, scoreboardobjective.getFormattedDisplayName(), collection.size()}), true);
        }

        return j;
    }

    private static int removeScore(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection, ScoreboardObjective scoreboardobjective, int i) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        int j = 0;

        ScoreboardScore scoreboardscore;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); j += scoreboardscore.getScore()) {
            String s = (String) iterator.next();

            scoreboardscore = scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective);
            scoreboardscore.setScore(scoreboardscore.getScore() - i);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.remove.success.single", new Object[]{i, scoreboardobjective.getFormattedDisplayName(), collection.iterator().next(), j}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.remove.success.multiple", new Object[]{i, scoreboardobjective.getFormattedDisplayName(), collection.size()}), true);
        }

        return j;
    }

    private static int listTrackedPlayers(CommandListenerWrapper commandlistenerwrapper) {
        Collection<String> collection = commandlistenerwrapper.getServer().getScoreboard().getTrackedPlayers();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.list.empty"), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.list.success", new Object[]{collection.size(), ChatComponentUtils.formatList(collection)}), false);
        }

        return collection.size();
    }

    private static int listTrackedPlayerScores(CommandListenerWrapper commandlistenerwrapper, String s) {
        Map<ScoreboardObjective, ScoreboardScore> map = commandlistenerwrapper.getServer().getScoreboard().getPlayerScores(s);

        if (map.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.list.entity.empty", new Object[]{s}), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.list.entity.success", new Object[]{s, map.size()}), false);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ScoreboardObjective, ScoreboardScore> entry = (Entry) iterator.next();

                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.players.list.entity.entry", new Object[]{((ScoreboardObjective) entry.getKey()).getFormattedDisplayName(), ((ScoreboardScore) entry.getValue()).getScore()}), false);
            }
        }

        return map.size();
    }

    private static int clearDisplaySlot(CommandListenerWrapper commandlistenerwrapper, int i) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        if (scoreboardserver.getDisplayObjective(i) == null) {
            throw CommandScoreboard.ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
        } else {
            scoreboardserver.setDisplayObjective(i, (ScoreboardObjective) null);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.display.cleared", new Object[]{Scoreboard.getDisplaySlotNames()[i]}), true);
            return 0;
        }
    }

    private static int setDisplaySlot(CommandListenerWrapper commandlistenerwrapper, int i, ScoreboardObjective scoreboardobjective) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        if (scoreboardserver.getDisplayObjective(i) == scoreboardobjective) {
            throw CommandScoreboard.ERROR_DISPLAY_SLOT_ALREADY_SET.create();
        } else {
            scoreboardserver.setDisplayObjective(i, scoreboardobjective);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.display.set", new Object[]{Scoreboard.getDisplaySlotNames()[i], scoreboardobjective.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setDisplayName(CommandListenerWrapper commandlistenerwrapper, ScoreboardObjective scoreboardobjective, IChatBaseComponent ichatbasecomponent) {
        if (!scoreboardobjective.getDisplayName().equals(ichatbasecomponent)) {
            scoreboardobjective.setDisplayName(ichatbasecomponent);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.modify.displayname", new Object[]{scoreboardobjective.getName(), scoreboardobjective.getFormattedDisplayName()}), true);
        }

        return 0;
    }

    private static int setRenderType(CommandListenerWrapper commandlistenerwrapper, ScoreboardObjective scoreboardobjective, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        if (scoreboardobjective.getRenderType() != iscoreboardcriteria_enumscoreboardhealthdisplay) {
            scoreboardobjective.setRenderType(iscoreboardcriteria_enumscoreboardhealthdisplay);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.modify.rendertype", new Object[]{scoreboardobjective.getFormattedDisplayName()}), true);
        }

        return 0;
    }

    private static int removeObjective(CommandListenerWrapper commandlistenerwrapper, ScoreboardObjective scoreboardobjective) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        scoreboardserver.removeObjective(scoreboardobjective);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.remove.success", new Object[]{scoreboardobjective.getFormattedDisplayName()}), true);
        return scoreboardserver.getObjectives().size();
    }

    private static int addObjective(CommandListenerWrapper commandlistenerwrapper, String s, IScoreboardCriteria iscoreboardcriteria, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        if (scoreboardserver.getObjective(s) != null) {
            throw CommandScoreboard.ERROR_OBJECTIVE_ALREADY_EXISTS.create();
        } else {
            scoreboardserver.addObjective(s, iscoreboardcriteria, ichatbasecomponent, iscoreboardcriteria.getDefaultRenderType());
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(s);

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.add.success", new Object[]{scoreboardobjective.getFormattedDisplayName()}), true);
            return scoreboardserver.getObjectives().size();
        }
    }

    private static int listObjectives(CommandListenerWrapper commandlistenerwrapper) {
        Collection<ScoreboardObjective> collection = commandlistenerwrapper.getServer().getScoreboard().getObjectives();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.list.empty"), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.scoreboard.objectives.list.success", new Object[]{collection.size(), ChatComponentUtils.formatList(collection, ScoreboardObjective::getFormattedDisplayName)}), false);
        }

        return collection.size();
    }
}
