package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class PlaceFeatureCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.placefeature.failed"));

    public PlaceFeatureCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("placefeature").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("feature", ResourceKeyArgument.key(IRegistry.CONFIGURED_FEATURE_REGISTRY)).executes((commandcontext) -> {
            return placeFeature((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getConfiguredFeature(commandcontext, "feature"), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()));
        })).then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return placeFeature((CommandListenerWrapper) commandcontext.getSource(), ResourceKeyArgument.getConfiguredFeature(commandcontext, "feature"), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"));
        }))));
    }

    public static int placeFeature(CommandListenerWrapper commandlistenerwrapper, Holder<WorldGenFeatureConfigured<?, ?>> holder, BlockPosition blockposition) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) holder.value();

        if (!worldgenfeatureconfigured.place(worldserver, worldserver.getChunkSource().getGenerator(), worldserver.getRandom(), blockposition)) {
            throw PlaceFeatureCommand.ERROR_FAILED.create();
        } else {
            String s = (String) holder.unwrapKey().map((resourcekey) -> {
                return resourcekey.location().toString();
            }).orElse("[unregistered]");

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.placefeature.success", new Object[]{s, blockposition.getX(), blockposition.getY(), blockposition.getZ()}), true);
            return 1;
        }
    }
}
