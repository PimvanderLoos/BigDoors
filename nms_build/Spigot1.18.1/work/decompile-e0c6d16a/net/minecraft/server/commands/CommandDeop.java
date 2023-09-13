package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
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

    private static final SimpleCommandExceptionType ERROR_NOT_OP = new SimpleCommandExceptionType(new ChatMessage("commands.deop.failed"));

    public CommandDeop() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("deop").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentProfile.gameProfile()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.suggest(((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getOpNames(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return deopPlayers((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.getGameProfiles(commandcontext, "targets"));
        })));
    }

    private static int deopPlayers(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection) throws CommandSyntaxException {
        PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (playerlist.isOp(gameprofile)) {
                playerlist.deop(gameprofile);
                ++i;
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.deop.success", new Object[]{((GameProfile) collection.iterator().next()).getName()}), true);
            }
        }

        if (i == 0) {
            throw CommandDeop.ERROR_NOT_OP.create();
        } else {
            commandlistenerwrapper.getServer().kickUnlistedPlayers(commandlistenerwrapper);
            return i;
        }
    }
}
