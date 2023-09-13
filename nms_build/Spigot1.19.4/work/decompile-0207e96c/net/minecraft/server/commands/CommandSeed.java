package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class CommandSeed {

    public CommandSeed() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher, boolean flag) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("seed").requires((commandlistenerwrapper) -> {
            return !flag || commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            long i = ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getSeed();
            IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.copyOnClickText(String.valueOf(i));

            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(IChatBaseComponent.translatable("commands.seed.success", ichatmutablecomponent), false);
            return (int) i;
        }));
    }
}
