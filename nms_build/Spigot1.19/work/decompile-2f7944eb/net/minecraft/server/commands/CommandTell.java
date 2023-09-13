package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.EntityPlayer;

public class CommandTell {

    public CommandTell() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralCommandNode<CommandListenerWrapper> literalcommandnode = commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("msg").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            return sendMessage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentChat.getChatMessage(commandcontext, "message"));
        }))));

        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("tell").redirect(literalcommandnode));
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("w").redirect(literalcommandnode));
    }

    private static int sendMessage(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, ArgumentChat.a argumentchat_a) {
        if (collection.isEmpty()) {
            return 0;
        } else {
            argumentchat_a.resolve(commandlistenerwrapper).thenAcceptAsync((filteredtext) -> {
                IChatBaseComponent ichatbasecomponent = ((PlayerChatMessage) filteredtext.raw()).serverContent();
                Iterator iterator = collection.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.message.display.outgoing", entityplayer.getDisplayName(), ichatbasecomponent).withStyle(EnumChatFormat.GRAY, EnumChatFormat.ITALIC), false);
                    PlayerChatMessage playerchatmessage = (PlayerChatMessage) filteredtext.filter(commandlistenerwrapper, entityplayer);

                    if (playerchatmessage != null) {
                        entityplayer.sendChatMessage(playerchatmessage, commandlistenerwrapper.asChatSender(), ChatMessageType.MSG_COMMAND);
                    }
                }

            }, commandlistenerwrapper.getServer());
            return collection.size();
        }
    }
}
