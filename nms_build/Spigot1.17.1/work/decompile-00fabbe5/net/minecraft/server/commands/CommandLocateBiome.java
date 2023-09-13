package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("locatebiome").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("biome", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CompletionProviders.AVAILABLE_BIOMES).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), (MinecraftKey) commandcontext.getArgument("biome", MinecraftKey.class));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, MinecraftKey minecraftkey) throws CommandSyntaxException {
        BiomeBase biomebase = (BiomeBase) commandlistenerwrapper.getServer().getCustomRegistry().d(IRegistry.BIOME_REGISTRY).getOptional(minecraftkey).orElseThrow(() -> {
            return CommandLocateBiome.ERROR_INVALID_BIOME.create(minecraftkey);
        });
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = commandlistenerwrapper.getWorld().a(biomebase, blockposition, 6400, 8);
        String s = minecraftkey.toString();

        if (blockposition1 == null) {
            throw CommandLocateBiome.ERROR_BIOME_NOT_FOUND.create(s);
        } else {
            return CommandLocate.a(commandlistenerwrapper, s, blockposition, blockposition1, "commands.locatebiome.success");
        }
    }
}
