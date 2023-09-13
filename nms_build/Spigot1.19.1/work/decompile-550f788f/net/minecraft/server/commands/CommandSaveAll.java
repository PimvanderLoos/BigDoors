package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;

public class CommandSaveAll {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.save.failed"));

    public CommandSaveAll() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("save-all").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            return saveAll((CommandListenerWrapper) commandcontext.getSource(), false);
        })).then(net.minecraft.commands.CommandDispatcher.literal("flush").executes((commandcontext) -> {
            return saveAll((CommandListenerWrapper) commandcontext.getSource(), true);
        })));
    }

    private static int saveAll(CommandListenerWrapper commandlistenerwrapper, boolean flag) throws CommandSyntaxException {
        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.save.saving"), false);
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        boolean flag1 = minecraftserver.saveEverything(true, flag, true);

        if (!flag1) {
            throw CommandSaveAll.ERROR_FAILED.create();
        } else {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.save.success"), true);
            return 1;
        }
    }
}
