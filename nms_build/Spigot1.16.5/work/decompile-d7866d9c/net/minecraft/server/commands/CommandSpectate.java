package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EnumGamemode;

public class CommandSpectate {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.spectate.self"));
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.spectate.not_spectator", new Object[]{object});
    });

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("spectate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), (Entity) null, ((CommandListenerWrapper) commandcontext.getSource()).h());
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("target", (ArgumentType) ArgumentEntity.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ((CommandListenerWrapper) commandcontext.getSource()).h());
        })).then(net.minecraft.commands.CommandDispatcher.a("player", (ArgumentType) ArgumentEntity.c()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.a(commandcontext, "target"), ArgumentEntity.e(commandcontext, "player"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, EntityPlayer entityplayer) throws CommandSyntaxException {
        if (entityplayer == entity) {
            throw CommandSpectate.a.create();
        } else if (entityplayer.playerInteractManager.getGameMode() != EnumGamemode.SPECTATOR) {
            throw CommandSpectate.b.create(entityplayer.getScoreboardDisplayName());
        } else {
            entityplayer.setSpectatorTarget(entity);
            if (entity != null) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.spectate.success.started", new Object[]{entity.getScoreboardDisplayName()}), false);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.spectate.success.stopped"), false);
            }

            return 1;
        }
    }
}
