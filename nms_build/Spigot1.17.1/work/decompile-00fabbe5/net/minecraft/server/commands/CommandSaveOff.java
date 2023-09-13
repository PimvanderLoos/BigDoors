package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;

public class CommandSaveOff {

    private static final SimpleCommandExceptionType ERROR_ALREADY_OFF = new SimpleCommandExceptionType(new ChatMessage("commands.save.alreadyOff"));

    public CommandSaveOff() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("save-off").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            CommandListenerWrapper commandlistenerwrapper = (CommandListenerWrapper) commandcontext.getSource();
            boolean flag = false;
            Iterator iterator = commandlistenerwrapper.getServer().getWorlds().iterator();

            while (iterator.hasNext()) {
                WorldServer worldserver = (WorldServer) iterator.next();

                if (worldserver != null && !worldserver.noSave) {
                    worldserver.noSave = true;
                    flag = true;
                }
            }

            if (!flag) {
                throw CommandSaveOff.ERROR_ALREADY_OFF.create();
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.disabled"), true);
                return 1;
            }
        }));
    }
}
