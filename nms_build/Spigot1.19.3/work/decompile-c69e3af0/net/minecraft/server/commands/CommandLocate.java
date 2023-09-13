package net.minecraft.server.commands;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
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
import org.slf4j.Logger;

public class CommandLocate {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DynamicCommandExceptionType ERROR_STRUCTURE_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.structure.not_found", object);
    });
    private static final DynamicCommandExceptionType ERROR_STRUCTURE_INVALID = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.structure.invalid", object);
    });
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.biome.not_found", object);
    });
    private static final DynamicCommandExceptionType ERROR_POI_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.locate.poi.not_found", object);
    });
    private static final int MAX_STRUCTURE_SEARCH_RADIUS = 100;
    private static final int MAX_BIOME_SEARCH_RADIUS = 6400;
    private static final int BIOME_SAMPLE_RESOLUTION_HORIZONTAL = 32;
    private static final int BIOME_SAMPLE_RESOLUTION_VERTICAL = 64;
    private static final int POI_SEARCH_RADIUS = 256;

    public CommandLocate() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher, CommandBuildContext commandbuildcontext) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("locate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("structure").then(net.minecraft.commands.CommandDispatcher.argument("structure", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.STRUCTURE)).executes((commandcontext) -> {
            return locateStructure((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagKeyArgument.getResourceOrTagKey(commandcontext, "structure", Registries.STRUCTURE, CommandLocate.ERROR_STRUCTURE_INVALID));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("biome").then(net.minecraft.commands.CommandDispatcher.argument("biome", ResourceOrTagArgument.resourceOrTag(commandbuildcontext, Registries.BIOME)).executes((commandcontext) -> {
            return locateBiome((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandcontext, "biome", Registries.BIOME));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("poi").then(net.minecraft.commands.CommandDispatcher.argument("poi", ResourceOrTagArgument.resourceOrTag(commandbuildcontext, Registries.POINT_OF_INTEREST_TYPE)).executes((commandcontext) -> {
            return locatePoi((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandcontext, "poi", Registries.POINT_OF_INTEREST_TYPE));
        }))));
    }

    private static Optional<? extends HolderSet.b<Structure>> getHolders(ResourceOrTagKeyArgument.c<Structure> resourceortagkeyargument_c, IRegistry<Structure> iregistry) {
        Either either = resourceortagkeyargument_c.unwrap();
        Function function = (resourcekey) -> {
            return iregistry.getHolder(resourcekey).map((holder) -> {
                return HolderSet.direct(holder);
            });
        };

        Objects.requireNonNull(iregistry);
        return (Optional) either.map(function, iregistry::getTag);
    }

    private static int locateStructure(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagKeyArgument.c<Structure> resourceortagkeyargument_c) throws CommandSyntaxException {
        IRegistry<Structure> iregistry = commandlistenerwrapper.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
        HolderSet<Structure> holderset = (HolderSet) getHolders(resourceortagkeyargument_c, iregistry).orElseThrow(() -> {
            return CommandLocate.ERROR_STRUCTURE_INVALID.create(resourceortagkeyargument_c.asPrintable());
        });
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Stopwatch stopwatch = Stopwatch.createStarted(SystemUtils.TICKER);
        Pair<BlockPosition, Holder<Structure>> pair = worldserver.getChunkSource().getGenerator().findNearestMapStructure(worldserver, holderset, blockposition, 100, false);

        stopwatch.stop();
        if (pair == null) {
            throw CommandLocate.ERROR_STRUCTURE_NOT_FOUND.create(resourceortagkeyargument_c.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortagkeyargument_c, blockposition, pair, "commands.locate.structure.success", false, stopwatch.elapsed());
        }
    }

    private static int locateBiome(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagArgument.c<BiomeBase> resourceortagargument_c) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        Stopwatch stopwatch = Stopwatch.createStarted(SystemUtils.TICKER);
        Pair<BlockPosition, Holder<BiomeBase>> pair = commandlistenerwrapper.getLevel().findClosestBiome3d(resourceortagargument_c, blockposition, 6400, 32, 64);

        stopwatch.stop();
        if (pair == null) {
            throw CommandLocate.ERROR_BIOME_NOT_FOUND.create(resourceortagargument_c.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortagargument_c, blockposition, pair, "commands.locate.biome.success", true, stopwatch.elapsed());
        }
    }

    private static int locatePoi(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagArgument.c<VillagePlaceType> resourceortagargument_c) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Stopwatch stopwatch = Stopwatch.createStarted(SystemUtils.TICKER);
        Optional<Pair<Holder<VillagePlaceType>, BlockPosition>> optional = worldserver.getPoiManager().findClosestWithType(resourceortagargument_c, blockposition, 256, VillagePlace.Occupancy.ANY);

        stopwatch.stop();
        if (optional.isEmpty()) {
            throw CommandLocate.ERROR_POI_NOT_FOUND.create(resourceortagargument_c.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortagargument_c, blockposition, ((Pair) optional.get()).swap(), "commands.locate.poi.success", false, stopwatch.elapsed());
        }
    }

    private static String getElementName(Pair<BlockPosition, ? extends Holder<?>> pair) {
        return (String) ((Holder) pair.getSecond()).unwrapKey().map((resourcekey) -> {
            return resourcekey.location().toString();
        }).orElse("[unregistered]");
    }

    public static int showLocateResult(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagArgument.c<?> resourceortagargument_c, BlockPosition blockposition, Pair<BlockPosition, ? extends Holder<?>> pair, String s, boolean flag, Duration duration) {
        String s1 = (String) resourceortagargument_c.unwrap().map((holder_c) -> {
            return resourceortagargument_c.asPrintable();
        }, (holderset_named) -> {
            String s2 = resourceortagargument_c.asPrintable();

            return s2 + " (" + getElementName(pair) + ")";
        });

        return showLocateResult(commandlistenerwrapper, blockposition, pair, s, flag, s1, duration);
    }

    public static int showLocateResult(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagKeyArgument.c<?> resourceortagkeyargument_c, BlockPosition blockposition, Pair<BlockPosition, ? extends Holder<?>> pair, String s, boolean flag, Duration duration) {
        String s1 = (String) resourceortagkeyargument_c.unwrap().map((resourcekey) -> {
            return resourcekey.location().toString();
        }, (tagkey) -> {
            MinecraftKey minecraftkey = tagkey.location();

            return "#" + minecraftkey + " (" + getElementName(pair) + ")";
        });

        return showLocateResult(commandlistenerwrapper, blockposition, pair, s, flag, s1, duration);
    }

    private static int showLocateResult(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, Pair<BlockPosition, ? extends Holder<?>> pair, String s, boolean flag, String s1, Duration duration) {
        BlockPosition blockposition1 = (BlockPosition) pair.getFirst();
        int i = flag ? MathHelper.floor(MathHelper.sqrt((float) blockposition.distSqr(blockposition1))) : MathHelper.floor(dist(blockposition.getX(), blockposition.getZ(), blockposition1.getX(), blockposition1.getZ()));
        String s2 = flag ? String.valueOf(blockposition1.getY()) : "~";
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.wrapInSquareBrackets(IChatBaseComponent.translatable("chat.coordinates", blockposition1.getX(), s2, blockposition1.getZ())).withStyle((chatmodifier) -> {
            return chatmodifier.withColor(EnumChatFormat.GREEN).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/tp @s " + blockposition1.getX() + " " + s2 + " " + blockposition1.getZ())).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, IChatBaseComponent.translatable("chat.coordinates.tooltip")));
        });

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable(s, s1, ichatmutablecomponent, i), false);
        CommandLocate.LOGGER.info("Locating element " + s1 + " took " + duration.toMillis() + " ms");
        return i;
    }

    private static float dist(int i, int j, int k, int l) {
        int i1 = k - i;
        int j1 = l - j;

        return MathHelper.sqrt((float) (i1 * i1 + j1 * j1));
    }
}
