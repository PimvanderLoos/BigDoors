package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;

public class CommandKick {

    public CommandKick() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("kick").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(3);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).executes((commandcontext) -> {
            return kickPlayers((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), new ChatMessage("multiplayer.disconnect.kicked"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("reason", ArgumentChat.message()).executes((commandcontext) -> {
            return kickPlayers((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentChat.getMessage(commandcontext, "reason"));
        }))));
    }

    private static int kickPlayers(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, IChatBaseComponent ichatbasecomponent) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.connection.disconnect(ichatbasecomponent);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.kick.success", new Object[]{entityplayer.getDisplayName(), ichatbasecomponent}), true);
        }

        return collection.size();
    }
}
