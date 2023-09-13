package net.minecraft.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandSeed {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher, boolean flag) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("seed").requires((commandlistenerwrapper) -> {
            return !flag || commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            long i = ((CommandListenerWrapper) commandcontext.getSource()).getWorld().getSeed();
            IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.a((IChatBaseComponent) (new ChatComponentText(String.valueOf(i))).format((chatmodifier) -> {
                return chatmodifier.setColor(EnumChatFormat.GREEN).setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, String.valueOf(i))).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.copy.click"))).setInsertion(String.valueOf(i));
            }));

            ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.seed.success", new Object[]{ichatmutablecomponent}), false);
            return (int) i;
        }));
    }
}
