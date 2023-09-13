package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.structure.Structure;

public class CommandLocate {

    private static final DynamicCommandExceptionType ERROR_STRUCTURE_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.structure.not_found", object);
    });
    private static final DynamicCommandExceptionType ERROR_STRUCTURE_INVALID = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.structure.invalid", object);
    });
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.biome.not_found", object);
    });
    private static final DynamicCommandExceptionType ERROR_BIOME_INVALID = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.biome.invalid", object);
    });
    private static final DynamicCommandExceptionType ERROR_POI_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.poi.not_found", object);
    });
    private static final DynamicCommandExceptionType ERROR_POI_INVALID = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.poi.invalid", object);
    });
    private static final int MAX_STRUCTURE_SEARCH_RADIUS = 100;
    private static final int MAX_BIOME_SEARCH_RADIUS = 6400;
    private static final int BIOME_SAMPLE_RESOLUTION_HORIZONTAL = 32;
    private static final int BIOME_SAMPLE_RESOLUTION_VERTICAL = 64;
    private static final int POI_SEARCH_RADIUS = 256;

    public CommandLocate() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("locate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("structure").then(net.minecraft.commands.CommandDispatcher.argument("structure", ResourceOrTagLocationArgument.resourceOrTag(IRegistry.STRUCTURE_REGISTRY)).executes((commandcontext) -> {
            return locateStructure((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagLocationArgument.getRegistryType(commandcontext, "structure", IRegistry.STRUCTURE_REGISTRY, CommandLocate.ERROR_STRUCTURE_INVALID));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("biome").then(net.minecraft.commands.CommandDispatcher.argument("biome", ResourceOrTagLocationArgument.resourceOrTag(IRegistry.BIOME_REGISTRY)).executes((commandcontext) -> {
            return locateBiome((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagLocationArgument.getRegistryType(commandcontext, "biome", IRegistry.BIOME_REGISTRY, CommandLocate.ERROR_BIOME_INVALID));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("poi").then(net.minecraft.commands.CommandDispatcher.argument("poi", ResourceOrTagLocationArgument.resourceOrTag(IRegistry.POINT_OF_INTEREST_TYPE_REGISTRY)).executes((commandcontext) -> {
            return locatePoi((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagLocationArgument.getRegistryType(commandcontext, "poi", IRegistry.POINT_OF_INTEREST_TYPE_REGISTRY, CommandLocate.ERROR_POI_INVALID));
        }))));
    }

    private static Optional<? extends HolderSet.b<Structure>> getHolders(ResourceOrTagLocationArgument.c<Structure> resourceortaglocationargument_c, IRegistry<Structure> iregistry) {
        Either either = resourceortaglocationargument_c.unwrap();
        Function function = (resourcekey) -> {
            return iregistry.getHolder(resourcekey).map((holder) -> {
                return HolderSet.direct(holder);
            });
        };

        Objects.requireNonNull(iregistry);
        return (Optional) either.map(function, iregistry::getTag);
    }

    private static int locateStructure(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.c<Structure> resourceortaglocationargument_c) throws CommandSyntaxException {
        IRegistry<Structure> iregistry = commandlistenerwrapper.getLevel().registryAccess().registryOrThrow(IRegistry.STRUCTURE_REGISTRY);
        HolderSet<Structure> holderset = (HolderSet) getHolders(resourceortaglocationargument_c, iregistry).orElseThrow(() -> {
            return CommandLocate.ERROR_STRUCTURE_INVALID.create(resourceortaglocationargument_c.asPrintable());
        });
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Pair<BlockPosition, Holder<Structure>> pair = worldserver.getChunkSource().getGenerator().findNearestMapStructure(worldserver, holderset, blockposition, 100, false);

        if (pair == null) {
            throw CommandLocate.ERROR_STRUCTURE_NOT_FOUND.create(resourceortaglocationargument_c.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortaglocationargument_c, blockposition, pair, "commands.locate.structure.success", false);
        }
    }

    private static int locateBiome(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.c<BiomeBase> resourceortaglocationargument_c) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        Pair<BlockPosition, Holder<BiomeBase>> pair = commandlistenerwrapper.getLevel().findClosestBiome3d(resourceortaglocationargument_c, blockposition, 6400, 32, 64);

        if (pair == null) {
            throw CommandLocate.ERROR_BIOME_NOT_FOUND.create(resourceortaglocationargument_c.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortaglocationargument_c, blockposition, pair, "commands.locate.biome.success", true);
        }
    }

    private static int locatePoi(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.c<VillagePlaceType> resourceortaglocationargument_c) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Optional<Pair<Holder<VillagePlaceType>, BlockPosition>> optional = worldserver.getPoiManager().findClosestWithType(resourceortaglocationargument_c, blockposition, 256, VillagePlace.Occupancy.ANY);

        if (optional.isEmpty()) {
            throw CommandLocate.ERROR_POI_NOT_FOUND.create(resourceortaglocationargument_c.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortaglocationargument_c, blockposition, ((Pair) optional.get()).swap(), "commands.locate.poi.success", false);
        }
    }

    public static int showLocateResult(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.c<?> resourceortaglocationargument_c, BlockPosition blockposition, Pair<BlockPosition, ? extends Holder<?>> pair, String s, boolean flag) {
        BlockPosition blockposition1 = (BlockPosition) pair.getFirst();
        String s1 = (String) resourceortaglocationargument_c.unwrap().map((resourcekey) -> {
            return resourcekey.location().toString();
        }, (tagkey) -> {
            MinecraftKey minecraftkey = tagkey.location();

            return "#" + minecraftkey + " (" + (String) ((Holder) pair.getSecond()).unwrapKey().map((resourcekey) -> {
                return resourcekey.location().toString();
            }).orElse("[unregistered]") + ")";
        });
        int i = flag ? MathHelper.floor(MathHelper.sqrt((float) blockposition.distSqr(blockposition1))) : MathHelper.floor(dist(blockposition.getX(), blockposition.getZ(), blockposition1.getX(), blockposition1.getZ()));
        String s2 = flag ? String.valueOf(blockposition1.getY()) : "~";
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.wrapInSquareBrackets(IChatBaseComponent.translatable("chat.coordinates", blockposition1.getX(), s2, blockposition1.getZ())).withStyle((chatmodifier) -> {
            return chatmodifier.withColor(EnumChatFormat.GREEN).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/tp @s " + blockposition1.getX() + " " + s2 + " " + blockposition1.getZ())).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.translatable("chat.coordinates.tooltip")));
        });

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable(s, s1, ichatmutablecomponent, i), false);
        return i;
    }

    private static float dist(int i, int j, int k, int l) {
        int i1 = k - i;
        int j1 = l - j;

        return MathHelper.sqrt((float) (i1 * i1 + j1 * j1));
    }
}
