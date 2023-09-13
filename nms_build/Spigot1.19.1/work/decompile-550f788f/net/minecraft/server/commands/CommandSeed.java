package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;

public class CommandSeed {

    public CommandSeed() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher, boolean flag) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("seed").requires((commandlistenerwrapper) -> {
            return !flag || commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            long i = ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getSeed();
            IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.wrapInSquareBrackets(IChatBaseComponent.literal(String.valueOf(i)).withStyle((chatmodifier) -> {
                return chatmodifier.withColor(EnumChatFormat.GREEN).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, String.valueOf(i))).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.translatable("chat.copy.click"))).withInsertion(String.valueOf(i));
            }));

            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(IChatBaseComponent.translatable("commands.seed.success", ichatmutablecomponent), false);
            return (int) i;
        }));
    }
}
