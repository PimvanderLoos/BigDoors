package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;

public class CommandMe {

    public CommandMe() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("me").then(net.minecraft.commands.CommandDispatcher.a("action", (ArgumentType) StringArgumentType.greedyString()).executes((commandcontext) -> {
            String s = StringArgumentType.getString(commandcontext, "action");
            Entity entity = ((CommandListenerWrapper) commandcontext.getSource()).getEntity();
            MinecraftServer minecraftserver = ((CommandListenerWrapper) commandcontext.getSource()).getServer();

            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) entity;

                    entityplayer.Q().a(s).thenAcceptAsync((itextfilter_a) -> {
                        String s1 = itextfilter_a.b();
                        IChatBaseComponent ichatbasecomponent = s1.isEmpty() ? null : a(commandcontext, s1);
                        IChatBaseComponent ichatbasecomponent1 = a(commandcontext, itextfilter_a.a());

                        minecraftserver.getPlayerList().a(ichatbasecomponent1, (entityplayer1) -> {
                            return entityplayer.b(entityplayer1) ? ichatbasecomponent : ichatbasecomponent1;
                        }, ChatMessageType.CHAT, entity.getUniqueID());
                    }, minecraftserver);
                    return 1;
                }

                minecraftserver.getPlayerList().sendMessage(a(commandcontext, s), ChatMessageType.CHAT, entity.getUniqueID());
            } else {
                minecraftserver.getPlayerList().sendMessage(a(commandcontext, s), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
            }

            return 1;
        })));
    }

    private static IChatBaseComponent a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return new ChatMessage("chat.type.emote", new Object[]{((CommandListenerWrapper) commandcontext.getSource()).getScoreboardDisplayName(), s});
    }
}
