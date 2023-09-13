package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentTime;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.valueproviders.IntProvider;

public class CommandWeather {

    private static final int DEFAULT_TIME = -1;

    public CommandWeather() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("weather").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("clear").executes((commandcontext) -> {
            return setClear((CommandListenerWrapper) commandcontext.getSource(), -1);
        })).then(net.minecraft.commands.CommandDispatcher.argument("duration", ArgumentTime.time(1)).executes((commandcontext) -> {
            return setClear((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("rain").executes((commandcontext) -> {
            return setRain((CommandListenerWrapper) commandcontext.getSource(), -1);
        })).then(net.minecraft.commands.CommandDispatcher.argument("duration", ArgumentTime.time(1)).executes((commandcontext) -> {
            return setRain((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("thunder").executes((commandcontext) -> {
            return setThunder((CommandListenerWrapper) commandcontext.getSource(), -1);
        })).then(net.minecraft.commands.CommandDispatcher.argument("duration", ArgumentTime.time(1)).executes((commandcontext) -> {
            return setThunder((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration"));
        }))));
    }

    private static int getDuration(CommandListenerWrapper commandlistenerwrapper, int i, IntProvider intprovider) {
        return i == -1 ? intprovider.sample(commandlistenerwrapper.getLevel().getRandom()) : i;
    }

    private static int setClear(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getLevel().setWeatherParameters(getDuration(commandlistenerwrapper, i, WorldServer.RAIN_DELAY), 0, false, false);
        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.weather.set.clear"), true);
        return i;
    }

    private static int setRain(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getLevel().setWeatherParameters(0, getDuration(commandlistenerwrapper, i, WorldServer.RAIN_DURATION), true, false);
        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.weather.set.rain"), true);
        return i;
    }

    private static int setThunder(CommandListenerWrapper commandlistenerwrapper, int i) {
        commandlistenerwrapper.getLevel().setWeatherParameters(0, getDuration(commandlistenerwrapper, i, WorldServer.THUNDER_DURATION), true, true);
        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.weather.set.thunder"), true);
        return i;
    }
}
