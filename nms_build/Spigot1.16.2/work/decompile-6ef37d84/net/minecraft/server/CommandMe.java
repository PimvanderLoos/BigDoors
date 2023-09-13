package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandMe {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) CommandDispatcher.a("me").then(CommandDispatcher.a("action", (ArgumentType) StringArgumentType.greedyString()).executes((commandcontext) -> {
            ChatMessage chatmessage = new ChatMessage("chat.type.emote", new Object[]{((CommandListenerWrapper) commandcontext.getSource()).getScoreboardDisplayName(), StringArgumentType.getString(commandcontext, "action")});
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
