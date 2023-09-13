package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;

public class CommandSaveOn {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.save.alreadyOn"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("save-on").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
            boolean flag = false;
            Iterator iterator = commandlistenerwrapper.getServer().getWorlds().iterator();

            while (iterator.hasNext()) {
                WorldServer worldserver = (WorldServer) iterator.next();

                if (worldserver != null && worldserver.savingDisabled) {
                    worldserver.savingDisabled = false;
                    flag = true;
                }
            }

            if (!flag) {
                throw CommandSaveOn.a.create();
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.enabled"), true);
                return 1;
            }
        }));
    }
}
