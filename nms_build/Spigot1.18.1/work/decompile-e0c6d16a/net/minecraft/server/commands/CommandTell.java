package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;

public class CommandTell {

    public CommandTell() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("msg").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            return sendMessage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentChat.getMessage(commandcontext, "message"));
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tell").redirect(literalcommandnode));
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("w").redirect(literalcommandnode));
    }

    private static int sendMessage(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, IChatBaseComponent ichatbasecomponent) {
        UUID uuid = commandlistenerwrapper.getEntity() == null ? SystemUtils.NIL_UUID : commandlistenerwrapper.getEntity().getUUID();
        Entity entity = commandlistenerwrapper.getEntity();
        Consumer consumer;

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            consumer = (ichatbasecomponent1) -> {
                entityplayer.sendMessage((new ChatMessage("commands.message.display.outgoing", new Object[]{ichatbasecomponent1, ichatbasecomponent})).withStyle(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}), entityplayer.getUUID());
            };
        } else {
            consumer = (ichatbasecomponent1) -> {
                commandlistenerwrapper.sendSuccess((new ChatMessage("commands.message.display.outgoing", new Object[]{ichatbasecomponent1, ichatbasecomponent})).withStyle(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}), false);
            };
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer1 = (EntityPlayer) iterator.next();

            consumer.accept(entityplayer1.getDisplayName());
            entityplayer1.sendMessage((new ChatMessage("commands.message.display.incoming", new Object[]{commandlistenerwrapper.getDisplayName(), ichatbasecomponent})).withStyle(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}), uuid);
        }

        return collection.size();
    }
}
