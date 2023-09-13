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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.players.PlayerList;

public class CommandDeop {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.deop.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("deop").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentProfile.a()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.a(((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().l(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.a(commandcontext, "targets"));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection) throws CommandSyntaxException {
        PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (playerlist.isOp(gameprofile)) {
                playerlist.removeOp(gameprofile);
                ++i;
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.deop.success", new Object[]{((GameProfile) collection.iterator().next()).getName()}), true);
            }
        }

        if (i == 0) {
            throw CommandDeop.a.create();
        } else {
            commandlistenerwrapper.getServer().a(commandlistenerwrapper);
            return i;
        }
    }
}
