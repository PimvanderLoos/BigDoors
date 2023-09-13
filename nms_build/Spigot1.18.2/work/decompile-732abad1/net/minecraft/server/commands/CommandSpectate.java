package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
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

    private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType(new ChatMessage("commands.spectate.self"));
    private static final DynamicCommandExceptionType ERROR_NOT_SPECTATOR = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.spectate.not_spectator", new Object[]{object});
    });

    public CommandSpectate() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("spectate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return spectate((CommandListenerWrapper) commandcontext.getSource(), (Entity) null, ((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException());
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("target", ArgumentEntity.entity()).executes((commandcontext) -> {
            return spectate((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException());
        })).then(net.minecraft.commands.CommandDispatcher.argument("player", ArgumentEntity.player()).executes((commandcontext) -> {
            return spectate((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ArgumentEntity.getPlayer(commandcontext, "player"));
        }))));
    }

    private static int spectate(CommandListenerWrapper commandlistenerwrapper, @Nullable Entity entity, EntityPlayer entityplayer) throws CommandSyntaxException {
        if (entityplayer == entity) {
            throw CommandSpectate.ERROR_SELF.create();
        } else if (entityplayer.gameMode.getGameModeForPlayer() != EnumGamemode.SPECTATOR) {
            throw CommandSpectate.ERROR_NOT_SPECTATOR.create(entityplayer.getDisplayName());
        } else {
            entityplayer.setCamera(entity);
            if (entity != null) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.spectate.success.started", new Object[]{entity.getDisplayName()}), false);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.spectate.success.stopped"), false);
            }

            return 1;
        }
    }
}
