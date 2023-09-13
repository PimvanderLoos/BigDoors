package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentVec2;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2F;

public class CommandWorldBorder {

    private static final SimpleCommandExceptionType ERROR_SAME_CENTER = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.center.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_SIZE = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.nochange"));
    private static final SimpleCommandExceptionType ERROR_TOO_SMALL = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.small"));
    private static final SimpleCommandExceptionType ERROR_TOO_BIG = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.big", new Object[]{5.9999968E7D}));
    private static final SimpleCommandExceptionType ERROR_TOO_FAR_OUT = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.set.failed.far", new Object[]{2.9999984E7D}));
    private static final SimpleCommandExceptionType ERROR_SAME_WARNING_TIME = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.warning.time.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_WARNING_DISTANCE = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.warning.distance.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_BUFFER = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.damage.buffer.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_AMOUNT = new SimpleCommandExceptionType(new ChatMessage("commands.worldborder.damage.amount.failed"));

    public CommandWorldBorder() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("worldborder").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("add").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("distance", DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D)).executes((commandcontext) -> {
            return setSize((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getWorldBorder().getSize() + DoubleArgumentType.getDouble(commandcontext, "distance"), 0L);
        })).then(net.minecraft.commands.CommandDispatcher.argument("time", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setSize((CommandListenerWrapper) commandcontext.getSource(), ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getWorldBorder().getSize() + DoubleArgumentType.getDouble(commandcontext, "distance"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getWorldBorder().getLerpRemainingTime() + (long) IntegerArgumentType.getInteger(commandcontext, "time") * 1000L);
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("set").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("distance", DoubleArgumentType.doubleArg(-5.9999968E7D, 5.9999968E7D)).executes((commandcontext) -> {
            return setSize((CommandListenerWrapper) commandcontext.getSource(), DoubleArgumentType.getDouble(commandcontext, "distance"), 0L);
        })).then(net.minecraft.commands.CommandDispatcher.argument("time", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setSize((CommandListenerWrapper) commandcontext.getSource(), DoubleArgumentType.getDouble(commandcontext, "distance"), (long) IntegerArgumentType.getInteger(commandcontext, "time") * 1000L);
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("center").then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentVec2.vec2()).executes((commandcontext) -> {
            return setCenter((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2.getVec2(commandcontext, "pos"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("damage").then(net.minecraft.commands.CommandDispatcher.literal("amount").then(net.minecraft.commands.CommandDispatcher.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((commandcontext) -> {
            return setDamageAmount((CommandListenerWrapper) commandcontext.getSource(), FloatArgumentType.getFloat(commandcontext, "damagePerBlock"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("buffer").then(net.minecraft.commands.CommandDispatcher.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((commandcontext) -> {
            return setDamageBuffer((CommandListenerWrapper) commandcontext.getSource(), FloatArgumentType.getFloat(commandcontext, "distance"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("get").executes((commandcontext) -> {
            return getSize((CommandListenerWrapper) commandcontext.getSource());
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("warning").then(net.minecraft.commands.CommandDispatcher.literal("distance").then(net.minecraft.commands.CommandDispatcher.argument("distance", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setWarningDistance((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "distance"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("time").then(net.minecraft.commands.CommandDispatcher.argument("time", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return setWarningTime((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "time"));
        })))));
    }

    private static int setDamageBuffer(CommandListenerWrapper commandlistenerwrapper, float f) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getServer().overworld().getWorldBorder();

        if (worldborder.getDamageSafeZone() == (double) f) {
            throw CommandWorldBorder.ERROR_SAME_DAMAGE_BUFFER.create();
        } else {
            worldborder.setDamageSafeZone((double) f);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.damage.buffer.success", new Object[]{String.format(Locale.ROOT, "%.2f", f)}), true);
            return (int) f;
        }
    }

    private static int setDamageAmount(CommandListenerWrapper commandlistenerwrapper, float f) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getServer().overworld().getWorldBorder();

        if (worldborder.getDamagePerBlock() == (double) f) {
            throw CommandWorldBorder.ERROR_SAME_DAMAGE_AMOUNT.create();
        } else {
            worldborder.setDamagePerBlock((double) f);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.damage.amount.success", new Object[]{String.format(Locale.ROOT, "%.2f", f)}), true);
            return (int) f;
        }
    }

    private static int setWarningTime(CommandListenerWrapper commandlistenerwrapper, int i) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getServer().overworld().getWorldBorder();

        if (worldborder.getWarningTime() == i) {
            throw CommandWorldBorder.ERROR_SAME_WARNING_TIME.create();
        } else {
            worldborder.setWarningTime(i);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.warning.time.success", new Object[]{i}), true);
            return i;
        }
    }

    private static int setWarningDistance(CommandListenerWrapper commandlistenerwrapper, int i) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getServer().overworld().getWorldBorder();

        if (worldborder.getWarningBlocks() == i) {
            throw CommandWorldBorder.ERROR_SAME_WARNING_DISTANCE.create();
        } else {
            worldborder.setWarningBlocks(i);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.warning.distance.success", new Object[]{i}), true);
            return i;
        }
    }

    private static int getSize(CommandListenerWrapper commandlistenerwrapper) {
        double d0 = commandlistenerwrapper.getServer().overworld().getWorldBorder().getSize();

        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.get", new Object[]{String.format(Locale.ROOT, "%.0f", d0)}), false);
        return MathHelper.floor(d0 + 0.5D);
    }

    private static int setCenter(CommandListenerWrapper commandlistenerwrapper, Vec2F vec2f) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getServer().overworld().getWorldBorder();

        if (worldborder.getCenterX() == (double) vec2f.x && worldborder.getCenterZ() == (double) vec2f.y) {
            throw CommandWorldBorder.ERROR_SAME_CENTER.create();
        } else if ((double) Math.abs(vec2f.x) <= 2.9999984E7D && (double) Math.abs(vec2f.y) <= 2.9999984E7D) {
            worldborder.setCenter((double) vec2f.x, (double) vec2f.y);
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.center.success", new Object[]{String.format(Locale.ROOT, "%.2f", vec2f.x), String.format("%.2f", vec2f.y)}), true);
            return 0;
        } else {
            throw CommandWorldBorder.ERROR_TOO_FAR_OUT.create();
        }
    }

    private static int setSize(CommandListenerWrapper commandlistenerwrapper, double d0, long i) throws CommandSyntaxException {
        WorldBorder worldborder = commandlistenerwrapper.getServer().overworld().getWorldBorder();
        double d1 = worldborder.getSize();

        if (d1 == d0) {
            throw CommandWorldBorder.ERROR_SAME_SIZE.create();
        } else if (d0 < 1.0D) {
            throw CommandWorldBorder.ERROR_TOO_SMALL.create();
        } else if (d0 > 5.9999968E7D) {
            throw CommandWorldBorder.ERROR_TOO_BIG.create();
        } else {
            if (i > 0L) {
                worldborder.lerpSizeBetween(d1, d0, i);
                if (d0 > d1) {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.set.grow", new Object[]{String.format(Locale.ROOT, "%.1f", d0), Long.toString(i / 1000L)}), true);
                } else {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.set.shrink", new Object[]{String.format(Locale.ROOT, "%.1f", d0), Long.toString(i / 1000L)}), true);
                }
            } else {
                worldborder.setSize(d0);
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.worldborder.set.immediate", new Object[]{String.format(Locale.ROOT, "%.1f", d0)}), true);
            }

            return (int) (d0 - d1);
        }
    }
}
