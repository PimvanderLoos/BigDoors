package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.arguments.ArgumentProfile;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.WhiteList;
import net.minecraft.server.players.WhiteListEntry;

public class CommandWhitelist {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType c = new SimpleCommandExceptionType(new ChatMessage("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("commands.whitelist.remove.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("whitelist").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("on").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("off").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("list").executes((commandcontext) -> {
            return d((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("add").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentProfile.a()).suggests((commandcontext, suggestionsbuilder) -> {
            PlayerList playerlist = ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList();

            return ICompletionProvider.b(playerlist.getPlayers().stream().filter((entityplayer) -> {
                return !playerlist.getWhitelist().isWhitelisted(entityplayer.getProfile());
            }).map((entityplayer) -> {
                return entityplayer.getProfile().getName();
            }), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.a(commandcontext, "targets"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("remove").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentProfile.a()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.a(((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getWhitelisted(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.a(commandcontext, "targets"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("reload").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) {
        commandlistenerwrapper.getServer().getPlayerList().reloadWhitelist();
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.reloaded"), true);
        commandlistenerwrapper.getServer().a(commandlistenerwrapper);
        return 1;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection) throws CommandSyntaxException {
        WhiteList whitelist = commandlistenerwrapper.getServer().getPlayerList().getWhitelist();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (!whitelist.isWhitelisted(gameprofile)) {
                WhiteListEntry whitelistentry = new WhiteListEntry(gameprofile);

                whitelist.add(whitelistentry);
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.add.success", new Object[]{ChatComponentUtils.a(gameprofile)}), true);
                ++i;
            }
        }

        if (i == 0) {
            throw CommandWhitelist.c.create();
        } else {
            return i;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection) throws CommandSyntaxException {
        WhiteList whitelist = commandlistenerwrapper.getServer().getPlayerList().getWhitelist();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (whitelist.isWhitelisted(gameprofile)) {
                WhiteListEntry whitelistentry = new WhiteListEntry(gameprofile);

                whitelist.b(whitelistentry);
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.remove.success", new Object[]{ChatComponentUtils.a(gameprofile)}), true);
                ++i;
            }
        }

        if (i == 0) {
            throw CommandWhitelist.d.create();
        } else {
            commandlistenerwrapper.getServer().a(commandlistenerwrapper);
            return i;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();

        if (playerlist.getHasWhitelist()) {
            throw CommandWhitelist.a.create();
        } else {
            playerlist.setHasWhitelist(true);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.enabled"), true);
            commandlistenerwrapper.getServer().a(commandlistenerwrapper);
            return 1;
        }
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper) throws CommandSyntaxException {
        PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();

        if (!playerlist.getHasWhitelist()) {
            throw CommandWhitelist.b.create();
        } else {
            playerlist.setHasWhitelist(false);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.disabled"), true);
            return 1;
        }
    }

    private static int d(CommandListenerWrapper commandlistenerwrapper) {
        String[] astring = commandlistenerwrapper.getServer().getPlayerList().getWhitelisted();

        if (astring.length == 0) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.none"), false);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.whitelist.list", new Object[]{astring.length, String.join(", ", astring)}), false);
        }

        return astring.length;
    }
}
