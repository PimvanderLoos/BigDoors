package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.players.IpBanList;

public class CommandPardonIP {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.pardonip.invalid"));
    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.pardonip.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pardon-ip").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.a("target", (ArgumentType) StringArgumentType.word()).suggests((commandcontext, suggestionsbuilder) -> {
            return ICompletionProvider.a(((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getIPBans().getEntries(), suggestionsbuilder);
        }).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "target"));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s) throws CommandSyntaxException {
        Matcher matcher = CommandBanIp.a.matcher(s);

        if (!matcher.matches()) {
            throw CommandPardonIP.a.create();
        } else {
            IpBanList ipbanlist = commandlistenerwrapper.getServer().getPlayerList().getIPBans();

            if (!ipbanlist.a(s)) {
                throw CommandPardonIP.b.create();
            } else {
                ipbanlist.remove(s);
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.pardonip.success", new Object[]{s}), true);
                return 1;
            }
        }
    }
}
