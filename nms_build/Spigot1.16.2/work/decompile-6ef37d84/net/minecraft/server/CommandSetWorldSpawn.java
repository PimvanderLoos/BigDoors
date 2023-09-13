package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class CommandSetWorldSpawn {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("setworldspawn").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.b(commandcontext, "pos"), 0.0F);
        })).then(CommandDispatcher.a("angle", (ArgumentType) ArgumentAngle.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.b(commandcontext, "pos"), ArgumentAngle.a(commandcontext, "angle"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, float f) {
        commandlistenerwrapper.getWorld().a(blockposition, f);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.setworldspawn.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), f}), true);
        return 1;
    }
}
