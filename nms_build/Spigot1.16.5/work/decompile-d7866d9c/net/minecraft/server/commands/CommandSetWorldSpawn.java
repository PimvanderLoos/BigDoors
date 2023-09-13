package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentAngle;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;

public class CommandSetWorldSpawn {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("setworldspawn").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.b(commandcontext, "pos"), 0.0F);
        })).then(net.minecraft.commands.CommandDispatcher.a("angle", (ArgumentType) ArgumentAngle.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.b(commandcontext, "pos"), ArgumentAngle.a(commandcontext, "angle"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, float f) {
        commandlistenerwrapper.getWorld().a(blockposition, f);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.setworldspawn.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), f}), true);
        return 1;
    }
}
