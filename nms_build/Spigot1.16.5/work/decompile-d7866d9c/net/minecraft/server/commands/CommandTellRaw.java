package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.server.level.EntityPlayer;

public class CommandTellRaw {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("tellraw").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("message", (ArgumentType) ArgumentChatComponent.a()).executes((commandcontext) -> {
            int i = 0;

            for (Iterator iterator = ArgumentEntity.f(commandcontext, "targets").iterator(); iterator.hasNext(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.sendMessage(ChatComponentUtils.filterForDisplay((CommandListenerWrapper) commandcontext.getSource(), ArgumentChatComponent.a(commandcontext, "message"), entityplayer, 0), SystemUtils.b);
            }

            return i;
        }))));
    }
}
