package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;

public class CommandSay {

    public CommandSay() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("say").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("message", ArgumentChat.message()).executes((commandcontext) -> {
            IChatBaseComponent ichatbasecomponent = ArgumentChat.getMessage(commandcontext, "message");
            ChatMessage chatmessage = new ChatMessage("chat.type.announcement", new Object[]{((CommandListenerWrapper) commandcontext.getSource()).getDisplayName(), ichatbasecomponent});
            Entity entity = ((CommandListenerWrapper) commandcontext.getSource()).getEntity();

            if (entity != null) {
                ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().broadcastMessage(chatmessage, ChatMessageType.CHAT, entity.getUUID());
            } else {
                ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().broadcastMessage(chatmessage, ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
            }

            return 1;
        })));
    }
}
