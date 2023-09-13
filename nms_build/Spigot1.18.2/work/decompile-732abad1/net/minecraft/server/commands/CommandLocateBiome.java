package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.level.biome.BiomeBase;

public class CommandLocateBiome {

    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locatebiome.notFound", new Object[]{object});
    });
    private static final int MAX_SEARCH_RADIUS = 6400;
    private static final int SEARCH_STEP = 8;

    public CommandLocateBiome() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("locatebiome").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("biome", ResourceOrTagLocationArgument.resourceOrTag(IRegistry.BIOME_REGISTRY)).executes((commandcontext) -> {
            return locateBiome((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagLocationArgument.getBiome(commandcontext, "biome"));
        })));
    }

    private static int locateBiome(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.b<BiomeBase> resourceortaglocationargument_b) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        Pair<BlockPosition, Holder<BiomeBase>> pair = commandlistenerwrapper.getLevel().findNearestBiome(resourceortaglocationargument_b, blockposition, 6400, 8);

        if (pair == null) {
            throw CommandLocateBiome.ERROR_BIOME_NOT_FOUND.create(resourceortaglocationargument_b.asPrintable());
        } else {
            return CommandLocate.showLocateResult(commandlistenerwrapper, resourceortaglocationargument_b, blockposition, pair, "commands.locatebiome.success");
        }
    }
}
