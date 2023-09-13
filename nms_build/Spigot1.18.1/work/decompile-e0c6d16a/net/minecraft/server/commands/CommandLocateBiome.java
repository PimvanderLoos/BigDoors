package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.biome.BiomeBase;

public class CommandLocateBiome {

    public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locatebiome.invalid", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locatebiome.notFound", new Object[]{object});
    });
    private static final int MAX_SEARCH_RADIUS = 6400;
    private static final int SEARCH_STEP = 8;

    public CommandLocateBiome() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("locatebiome").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("biome", ArgumentMinecraftKeyRegistered.id()).suggests(CompletionProviders.AVAILABLE_BIOMES).executes((commandcontext) -> {
            return locateBiome((CommandListenerWrapper) commandcontext.getSource(), (MinecraftKey) commandcontext.getArgument("biome", MinecraftKey.class));
        })));
    }

    private static int locateBiome(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey) throws CommandSyntaxException {
        BiomeBase biomebase = (BiomeBase) commandlistenerwrapper.getServer().registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY).getOptional(minecraftkey).orElseThrow(() -> {
            return CommandLocateBiome.ERROR_INVALID_BIOME.create(minecraftkey);
        });
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = commandlistenerwrapper.getLevel().findNearestBiome(biomebase, blockposition, 6400, 8);
        String s = minecraftkey.toString();

        if (blockposition1 == null) {
            throw CommandLocateBiome.ERROR_BIOME_NOT_FOUND.create(s);
        } else {
            return CommandLocate.showLocateResult(commandlistenerwrapper, s, blockposition, blockposition1, "commands.locatebiome.success");
        }
    }
}
