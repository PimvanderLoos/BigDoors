package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("msg").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(net.minecraft.commands.CommandDispatcher.a("message", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentChat.a(commandcontext, "message"));
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("tell").redirect(literalcommandnode));
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("w").redirect(literalcommandnode));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, IChatBaseComponent ichatbasecomponent) {
        UUID uuid = commandlistenerwrapper.getEntity() == null ? SystemUtils.b : commandlistenerwrapper.getEntity().getUniqueID();
        Entity entity = commandlistenerwrapper.getEntity();
        Consumer consumer;

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            consumer = (ichatbasecomponent1) -> {
                entityplayer.sendMessage((new ChatMessage("commands.message.display.outgoing", new Object[]{ichatbasecomponent1, ichatbasecomponent})).a(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}), entityplayer.getUniqueID());
            };
        } else {
            consumer = (ichatbasecomponent1) -> {
                commandlistenerwrapper.sendMessage((new ChatMessage("commands.message.display.outgoing", new Object[]{ichatbasecomponent1, ichatbasecomponent})).a(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}), false);
            };
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer1 = (EntityPlayer) iterator.next();

            consumer.accept(entityplayer1.getScoreboardDisplayName());
            entityplayer1.sendMessage((new ChatMessage("commands.message.display.incoming", new Object[]{commandlistenerwrapper.getScoreboardDisplayName(), ichatbasecomponent})).a(new EnumChatFormat[]{EnumChatFormat.GRAY, EnumChatFormat.ITALIC}), uuid);
        }

        return collection.size();
    }
}
