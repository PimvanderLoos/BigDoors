package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;

public class CommandIdleTimeout {

    public CommandIdleTimeout() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("setidletimeout").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(net.minecraft.commands.CommandDispatcher.argument("minutes", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setIdleTimeout((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "minutes"));
        })));
    }

    private static int setIdleTimeout(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getServer().setPlayerIdleTimeout(i);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.setidletimeout.success", new Object[]{i}), true);
        return i;
    }
}
