package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentChatFormat;
import net.minecraft.commands.arguments.ArgumentScoreboardTeam;
import net.minecraft.commands.arguments.ArgumentScoreholder;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;

public class CommandTeam {

    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(new ChatMessage("commands.team.add.duplicate"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType(new ChatMessage("commands.team.empty.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.name.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.color.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.friendlyfire.alreadyEnabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.friendlyfire.alreadyDisabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.nametagVisibility.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.deathMessageVisibility.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType(new ChatMessage("commands.team.option.collisionRule.unchanged"));

    public CommandTeam() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("team").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("list").executes((commandcontext) -> {
            return listTeams((CommandListenerWrapper) commandcontext.getSource());
        })).then(net.minecraft.commands.CommandDispatcher.argument("team", ArgumentScoreboardTeam.team()).executes((commandcontext) -> {
            return listMembers((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("add").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("team", StringArgumentType.word()).executes((commandcontext) -> {
            return createTeam((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "team"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("displayName", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return createTeam((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "team"), ArgumentChatComponent.getComponent(commandcontext, "displayName"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("remove").then(net.minecraft.commands.CommandDispatcher.argument("team", ArgumentScoreboardTeam.team()).executes((commandcontext) -> {
            return deleteTeam((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("empty").then(net.minecraft.commands.CommandDispatcher.argument("team", ArgumentScoreboardTeam.team()).executes((commandcontext) -> {
            return emptyTeam((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("join").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("team", ArgumentScoreboardTeam.team()).executes((commandcontext) -> {
            return joinTeam((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).getEntityOrException().getScoreboardName()));
        })).then(net.minecraft.commands.CommandDispatcher.argument("members", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).executes((commandcontext) -> {
            return joinTeam((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "members"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("leave").then(net.minecraft.commands.CommandDispatcher.argument("members", ArgumentScoreholder.scoreHolders()).suggests(ArgumentScoreholder.SUGGEST_SCORE_HOLDERS).executes((commandcontext) -> {
            return leaveTeam((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreholder.getNamesWithDefaultWildcard(commandcontext, "members"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("modify").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("team", ArgumentScoreboardTeam.team()).then(net.minecraft.commands.CommandDispatcher.literal("displayName").then(net.minecraft.commands.CommandDispatcher.argument("displayName", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return setDisplayName((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ArgumentChatComponent.getComponent(commandcontext, "displayName"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("color").then(net.minecraft.commands.CommandDispatcher.argument("value", ArgumentChatFormat.color()).executes((commandcontext) -> {
            return setColor((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ArgumentChatFormat.getColor(commandcontext, "value"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("friendlyFire").then(net.minecraft.commands.CommandDispatcher.argument("allowed", BoolArgumentType.bool()).executes((commandcontext) -> {
            return setFriendlyFire((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), BoolArgumentType.getBool(commandcontext, "allowed"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("seeFriendlyInvisibles").then(net.minecraft.commands.CommandDispatcher.argument("allowed", BoolArgumentType.bool()).executes((commandcontext) -> {
            return setFriendlySight((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), BoolArgumentType.getBool(commandcontext, "allowed"));
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("nametagVisibility").then(net.minecraft.commands.CommandDispatcher.literal("never").executes((commandcontext) -> {
            return setNametagVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("hideForOtherTeams").executes((commandcontext) -> {
            return setNametagVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("hideForOwnTeam").executes((commandcontext) -> {
            return setNametagVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("always").executes((commandcontext) -> {
            return setNametagVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("deathMessageVisibility").then(net.minecraft.commands.CommandDispatcher.literal("never").executes((commandcontext) -> {
            return setDeathMessageVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("hideForOtherTeams").executes((commandcontext) -> {
            return setDeathMessageVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("hideForOwnTeam").executes((commandcontext) -> {
            return setDeathMessageVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("always").executes((commandcontext) -> {
            return setDeathMessageVisibility((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("collisionRule").then(net.minecraft.commands.CommandDispatcher.literal("never").executes((commandcontext) -> {
            return setCollision((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumTeamPush.NEVER);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("pushOwnTeam").executes((commandcontext) -> {
            return setCollision((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumTeamPush.PUSH_OWN_TEAM);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("pushOtherTeams").executes((commandcontext) -> {
            return setCollision((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumTeamPush.PUSH_OTHER_TEAMS);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("always").executes((commandcontext) -> {
            return setCollision((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ScoreboardTeamBase.EnumTeamPush.ALWAYS);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("prefix").then(net.minecraft.commands.CommandDispatcher.argument("prefix", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return setPrefix((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ArgumentChatComponent.getComponent(commandcontext, "prefix"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("suffix").then(net.minecraft.commands.CommandDispatcher.argument("suffix", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            return setSuffix((CommandListenerWrapper) commandcontext.getSource(), ArgumentScoreboardTeam.getTeam(commandcontext, "team"), ArgumentChatComponent.getComponent(commandcontext, "suffix"));
        }))))));
    }

    private static int leaveTeam(CommandListenerWrapper commandlistenerwrapper, Collection<String> collection) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            scoreboardserver.removePlayerFromTeam(s);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.leave.success.single", new Object[]{collection.iterator().next()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.leave.success.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }

    private static int joinTeam(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, Collection<String> collection) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            scoreboardserver.addPlayerToTeam(s, scoreboardteam);
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.join.success.single", new Object[]{collection.iterator().next(), scoreboardteam.getFormattedDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.join.success.multiple", new Object[]{collection.size(), scoreboardteam.getFormattedDisplayName()}), true);
        }

        return collection.size();
    }

    private static int setNametagVisibility(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility) throws CommandSyntaxException {
        if (scoreboardteam.getNameTagVisibility() == scoreboardteambase_enumnametagvisibility) {
            throw CommandTeam.ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
        } else {
            scoreboardteam.setNameTagVisibility(scoreboardteambase_enumnametagvisibility);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.nametagVisibility.success", new Object[]{scoreboardteam.getFormattedDisplayName(), scoreboardteambase_enumnametagvisibility.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setDeathMessageVisibility(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility) throws CommandSyntaxException {
        if (scoreboardteam.getDeathMessageVisibility() == scoreboardteambase_enumnametagvisibility) {
            throw CommandTeam.ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
        } else {
            scoreboardteam.setDeathMessageVisibility(scoreboardteambase_enumnametagvisibility);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.deathMessageVisibility.success", new Object[]{scoreboardteam.getFormattedDisplayName(), scoreboardteambase_enumnametagvisibility.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setCollision(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, ScoreboardTeamBase.EnumTeamPush scoreboardteambase_enumteampush) throws CommandSyntaxException {
        if (scoreboardteam.getCollisionRule() == scoreboardteambase_enumteampush) {
            throw CommandTeam.ERROR_TEAM_COLLISION_UNCHANGED.create();
        } else {
            scoreboardteam.setCollisionRule(scoreboardteambase_enumteampush);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.collisionRule.success", new Object[]{scoreboardteam.getFormattedDisplayName(), scoreboardteambase_enumteampush.getDisplayName()}), true);
            return 0;
        }
    }

    private static int setFriendlySight(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, boolean flag) throws CommandSyntaxException {
        if (scoreboardteam.canSeeFriendlyInvisibles() == flag) {
            if (flag) {
                throw CommandTeam.ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
            } else {
                throw CommandTeam.ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
            }
        } else {
            scoreboardteam.setSeeFriendlyInvisibles(flag);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.seeFriendlyInvisibles." + (flag ? "enabled" : "disabled"), new Object[]{scoreboardteam.getFormattedDisplayName()}), true);
            return 0;
        }
    }

    private static int setFriendlyFire(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, boolean flag) throws CommandSyntaxException {
        if (scoreboardteam.isAllowFriendlyFire() == flag) {
            if (flag) {
                throw CommandTeam.ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
            } else {
                throw CommandTeam.ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
            }
        } else {
            scoreboardteam.setAllowFriendlyFire(flag);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.friendlyfire." + (flag ? "enabled" : "disabled"), new Object[]{scoreboardteam.getFormattedDisplayName()}), true);
            return 0;
        }
    }

    private static int setDisplayName(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        if (scoreboardteam.getDisplayName().equals(ichatbasecomponent)) {
            throw CommandTeam.ERROR_TEAM_ALREADY_NAME.create();
        } else {
            scoreboardteam.setDisplayName(ichatbasecomponent);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.name.success", new Object[]{scoreboardteam.getFormattedDisplayName()}), true);
            return 0;
        }
    }

    private static int setColor(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, EnumChatFormat enumchatformat) throws CommandSyntaxException {
        if (scoreboardteam.getColor() == enumchatformat) {
            throw CommandTeam.ERROR_TEAM_ALREADY_COLOR.create();
        } else {
            scoreboardteam.setColor(enumchatformat);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.color.success", new Object[]{scoreboardteam.getFormattedDisplayName(), enumchatformat.getName()}), true);
            return 0;
        }
    }

    private static int emptyTeam(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();
        Collection<String> collection = Lists.newArrayList(scoreboardteam.getPlayers());

        if (collection.isEmpty()) {
            throw CommandTeam.ERROR_TEAM_ALREADY_EMPTY.create();
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                scoreboardserver.removePlayerFromTeam(s, scoreboardteam);
            }

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.empty.success", new Object[]{collection.size(), scoreboardteam.getFormattedDisplayName()}), true);
            return collection.size();
        }
    }

    private static int deleteTeam(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam) {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        scoreboardserver.removePlayerTeam(scoreboardteam);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.remove.success", new Object[]{scoreboardteam.getFormattedDisplayName()}), true);
        return scoreboardserver.getPlayerTeams().size();
    }

    private static int createTeam(CommandListenerWrapper commandlistenerwrapper, String s) throws CommandSyntaxException {
        return createTeam(commandlistenerwrapper, s, new ChatComponentText(s));
    }

    private static int createTeam(CommandListenerWrapper commandlistenerwrapper, String s, IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        ScoreboardServer scoreboardserver = commandlistenerwrapper.getServer().getScoreboard();

        if (scoreboardserver.getPlayerTeam(s) != null) {
            throw CommandTeam.ERROR_TEAM_ALREADY_EXISTS.create();
        } else {
            ScoreboardTeam scoreboardteam = scoreboardserver.addPlayerTeam(s);

            scoreboardteam.setDisplayName(ichatbasecomponent);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.add.success", new Object[]{scoreboardteam.getFormattedDisplayName()}), true);
            return scoreboardserver.getPlayerTeams().size();
        }
    }

    private static int listMembers(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam) {
        Collection<String> collection = scoreboardteam.getPlayers();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.list.members.empty", new Object[]{scoreboardteam.getFormattedDisplayName()}), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.list.members.success", new Object[]{scoreboardteam.getFormattedDisplayName(), collection.size(), ChatComponentUtils.formatList(collection)}), false);
        }

        return collection.size();
    }

    private static int listTeams(CommandListenerWrapper commandlistenerwrapper) {
        Collection<ScoreboardTeam> collection = commandlistenerwrapper.getServer().getScoreboard().getPlayerTeams();

        if (collection.isEmpty()) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.list.teams.empty"), false);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.list.teams.success", new Object[]{collection.size(), ChatComponentUtils.formatList(collection, ScoreboardTeam::getFormattedDisplayName)}), false);
        }

        return collection.size();
    }

    private static int setPrefix(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, IChatBaseComponent ichatbasecomponent) {
        scoreboardteam.setPlayerPrefix(ichatbasecomponent);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.prefix.success", new Object[]{ichatbasecomponent}), false);
        return 1;
    }

    private static int setSuffix(CommandListenerWrapper commandlistenerwrapper, ScoreboardTeam scoreboardteam, IChatBaseComponent ichatbasecomponent) {
        scoreboardteam.setPlayerSuffix(ichatbasecomponent);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.team.option.suffix.success", new Object[]{ichatbasecomponent}), false);
        return 1;
    }
}
