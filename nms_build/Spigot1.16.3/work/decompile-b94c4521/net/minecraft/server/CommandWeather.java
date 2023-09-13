package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandWeather {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("weather").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) CommandDispatcher.a("clear").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), 6000);
        })).then(CommandDispatcher.a("duration", (ArgumentType) IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration") * 20);
        })))).then(((LiteralArgumentBuilder) CommandDispatcher.a("rain").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), 6000);
        })).then(CommandDispatcher.a("duration", (ArgumentType) IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration") * 20);
        })))).then(((LiteralArgumentBuilder) CommandDispatcher.a("thunder").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource(), 6000);
        })).then(CommandDispatcher.a("duration", (ArgumentType) IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration") * 20);
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getWorld().a(i, 0, false, false);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.weather.set.clear"), true);
        return i;
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getWorld().a(0, i, true, false);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.weather.set.rain"), true);
        return i;
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getWorld().a(0, i, true, true);
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.weather.set.thunder"), true);
        return i;
    }
}
