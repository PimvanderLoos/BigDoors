package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentAngle;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;

public class CommandSetWorldSpawn {

    public CommandSetWorldSpawn() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("setworldspawn").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return setSpawn((CommandListenerWrapper) commandcontext.getSource(), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return setSpawn((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getSpawnablePos(commandcontext, "pos"), 0.0F);
        })).then(net.minecraft.commands.CommandDispatcher.argument("angle", ArgumentAngle.angle()).executes((commandcontext) -> {
            return setSpawn((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getSpawnablePos(commandcontext, "pos"), ArgumentAngle.getAngle(commandcontext, "angle"));
        }))));
    }

    private static int setSpawn(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, float f) {
        commandlistenerwrapper.getLevel().setDefaultSpawnPos(blockposition, f);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.setworldspawn.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), f}), true);
        return 1;
    }
}
