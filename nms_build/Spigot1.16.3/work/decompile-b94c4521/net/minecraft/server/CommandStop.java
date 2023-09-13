package net.minecraft.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandStop {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("stop").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.stop.stopping"), true);
            ((CommandListenerWrapper) commandcontext.getSource()).getServer().safeShutdown(false);
            return 1;
        }));
    }
}
