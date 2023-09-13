package net.minecraft.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;

public class CommandSaveOff {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.save.alreadyOff", new Object[0]));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("save-off").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
            boolean flag = false;
            WorldServer[] aworldserver = commandlistenerwrapper.getServer().worldServer;
            int i = aworldserver.length;

            for (int j = 0; j < i; ++j) {
                WorldServer worldserver = aworldserver[j];

                if (worldserver != null && !worldserver.savingDisabled) {
                    worldserver.savingDisabled = true;
                    flag = true;
                }
            }

            if (!flag) {
                throw CommandSaveOff.a.create();
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.disabled", new Object[0]), true);
                return 1;
            }
        }));
    }
}
