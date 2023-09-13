package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.MinecraftServer;

public class CommandSaveAll {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.save.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("save-all").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), false);
        })).then(net.minecraft.commands.CommandDispatcher.a("flush").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), true);
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, boolean flag) throws CommandSyntaxException {
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.saving"), false);
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        minecraftserver.getPlayerList().savePlayers();
        boolean flag1 = minecraftserver.saveChunks(true, flag, true);

        if (!flag1) {
            throw CommandSaveAll.a.create();
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.success"), true);
            return 1;
        }
    }
}
