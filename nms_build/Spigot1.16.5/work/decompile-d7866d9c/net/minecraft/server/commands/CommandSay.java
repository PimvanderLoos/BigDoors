package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentChat;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;

public class CommandSay {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("say").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("message", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            IChatBaseComponent ichatbasecomponent = ArgumentChat.a(commandcontext, "message");
            ChatMessage chatmessage = new ChatMessage("chat.type.announcement", new Object[]{((CommandListenerWrapper) commandcontext.getSource()).getScoreboardDisplayName(), ichatbasecomponent});
            Entity entity = ((CommandListenerWrapper) commandcontext.getSource()).getEntity();

            if (entity != null) {
                ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().sendMessage(chatmessage, ChatMessageType.CHAT, entity.getUniqueID());
            } else {
                ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().sendMessage(chatmessage, ChatMessageType.SYSTEM, SystemUtils.b);
            }

            return 1;
        })));
    }
}
