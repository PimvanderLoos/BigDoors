package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChatComponent;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.server.level.EntityPlayer;

public class CommandTellRaw {

    public CommandTellRaw() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tellraw").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChatComponent.textComponent()).executes((commandcontext) -> {
            int i = 0;

            for (Iterator iterator = ArgumentEntity.getPlayers(commandcontext, "targets").iterator(); iterator.hasNext(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.sendSystemMessage(ChatComponentUtils.updateForEntity((CommandListenerWrapper) commandcontext.getSource(), ArgumentChatComponent.getComponent(commandcontext, "message"), entityplayer, 0), false);
            }

            return i;
        }))));
    }
}
