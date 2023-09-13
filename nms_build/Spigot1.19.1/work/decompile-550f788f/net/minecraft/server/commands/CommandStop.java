package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;

public class CommandStop {

    public CommandStop() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("stop").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(IChatBaseComponent.translatable("commands.stop.stopping"), true);
            ((CommandListenerWrapper) commandcontext.getSource()).getServer().halt(false);
            return 1;
        }));
    }
}
