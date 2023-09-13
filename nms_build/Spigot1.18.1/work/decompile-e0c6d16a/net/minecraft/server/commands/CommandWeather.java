package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;

public class CommandWeather {

    private static final int DEFAULT_TIME = 6000;

    public CommandWeather() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("weather").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("clear").executes((commandcontext) -> {
            return setClear((CommandListenerWrapper) commandcontext.getSource(), 6000);
        })).then(net.minecraft.commands.CommandDispatcher.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
            return setClear((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration") * 20);
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("rain").executes((commandcontext) -> {
            return setRain((CommandListenerWrapper) commandcontext.getSource(), 6000);
        })).then(net.minecraft.commands.CommandDispatcher.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
            return setRain((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration") * 20);
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("thunder").executes((commandcontext) -> {
            return setThunder((CommandListenerWrapper) commandcontext.getSource(), 6000);
        })).then(net.minecraft.commands.CommandDispatcher.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
            return setThunder((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration") * 20);
        }))));
    }

    private static int setClear(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getLevel().setWeatherParameters(i, 0, false, false);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.weather.set.clear"), true);
        return i;
    }

    private static int setRain(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getLevel().setWeatherParameters(0, i, true, false);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.weather.set.rain"), true);
        return i;
    }

    private static int setThunder(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getLevel().setWeatherParameters(0, i, true, true);
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.weather.set.thunder"), true);
        return i;
    }
}
