package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandSay {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("say").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(CommandDispatcher.a("message", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
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
