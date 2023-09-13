package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;

public class CommandTell {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("msg").then(CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(CommandDispatcher.a("message", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentChat.a(commandcontext, "message"));
        }))));

        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("tell").redirect(literalcommandnode));
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("w").redirect(literalcommandnode));
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
