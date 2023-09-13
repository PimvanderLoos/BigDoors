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
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class CommandLocate {

    private static final DynamicCommandExceptionType ERROR_FAILED = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locate.failed", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.locate.invalid", new Object[]{object});
    });

    public CommandLocate() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("locate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("structure", ResourceOrTagLocationArgument.resourceOrTag(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY)).executes((commandcontext) -> {
            return locate((CommandListenerWrapper) commandcontext.getSource(), ResourceOrTagLocationArgument.getStructureFeature(commandcontext, "structure"));
        })));
    }

    private static int locate(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.b<StructureFeature<?, ?>> resourceortaglocationargument_b) throws CommandSyntaxException {
        IRegistry<StructureFeature<?, ?>> iregistry = commandlistenerwrapper.getLevel().registryAccess().registryOrThrow(IRegistry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        Either either = resourceortaglocationargument_b.unwrap();
        Function function = (resourcekey) -> {
            return iregistry.getHolder(resourcekey).map((holder) -> {
                return HolderSet.direct(holder);
            });
        };

        Objects.requireNonNull(iregistry);
        HolderSet<StructureFeature<?, ?>> holderset = (HolderSet) ((Optional) either.map(function, iregistry::getTag)).orElseThrow(() -> {
            return CommandLocate.ERROR_INVALID.create(resourceortaglocationargument_b.asPrintable());
        });
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Pair<BlockPosition, Holder<StructureFeature<?, ?>>> pair = worldserver.getChunkSource().getGenerator().findNearestMapFeature(worldserver, holderset, blockposition, 100, false);

        if (pair == null) {
            throw CommandLocate.ERROR_FAILED.create(resourceortaglocationargument_b.asPrintable());
        } else {
            return showLocateResult(commandlistenerwrapper, resourceortaglocationargument_b, blockposition, pair, "commands.locate.success");
        }
    }

    public static int showLocateResult(CommandListenerWrapper commandlistenerwrapper, ResourceOrTagLocationArgument.b<?> resourceortaglocationargument_b, BlockPosition blockposition, Pair<BlockPosition, ? extends Holder<?>> pair, String s) {
        BlockPosition blockposition1 = (BlockPosition) pair.getFirst();
        String s1 = (String) resourceortaglocationargument_b.unwrap().map((resourcekey) -> {
            return resourcekey.location().toString();
        }, (tagkey) -> {
            MinecraftKey minecraftkey = tagkey.location();

            return "#" + minecraftkey + " (" + (String) ((Holder) pair.getSecond()).unwrapKey().map((resourcekey) -> {
                return resourcekey.location().toString();
            }).orElse("[unregistered]") + ")";
        });
        int i = MathHelper.floor(dist(blockposition.getX(), blockposition.getZ(), blockposition1.getX(), blockposition1.getZ()));
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.wrapInSquareBrackets(new ChatMessage("chat.coordinates", new Object[]{blockposition1.getX(), "~", blockposition1.getZ()})).withStyle((chatmodifier) -> {
            ChatModifier chatmodifier1 = chatmodifier.withColor(EnumChatFormat.GREEN);
            ChatClickable.EnumClickAction chatclickable_enumclickaction = ChatClickable.EnumClickAction.SUGGEST_COMMAND;
            int j = blockposition1.getX();

            return chatmodifier1.withClickEvent(new ChatClickable(chatclickable_enumclickaction, "/tp @s " + j + " ~ " + blockposition1.getZ())).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.coordinates.tooltip")));
        });

        commandlistenerwrapper.sendSuccess(new ChatMessage(s, new Object[]{s1, ichatmutablecomponent, i}), false);
        return i;
    }

    private static float dist(int i, int j, int k, int l) {
        int i1 = k - i;
        int j1 = l - j;

        return MathHelper.sqrt((float) (i1 * i1 + j1 * j1));
    }
}
