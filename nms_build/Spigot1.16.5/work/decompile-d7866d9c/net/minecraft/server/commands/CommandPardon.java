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
import net.minecraft.server.players.GameProfileBanList;

public class CommandPardon {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.pardon.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pardon").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentProfile.a()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.a(((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getProfileBans().getEntries(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.a(commandcontext, "targets"));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection) throws CommandSyntaxException {
        GameProfileBanList gameprofilebanlist = commandlistenerwrapper.getServer().getPlayerList().getProfileBans();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (gameprofilebanlist.isBanned(gameprofile)) {
                gameprofilebanlist.remove(gameprofile);
                ++i;
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.pardon.success", new Object[]{ChatComponentUtils.a(gameprofile)}), true);
            }
        }

        if (i == 0) {
            throw CommandPardon.a.create();
        } else {
            return i;
        }
    }
}
