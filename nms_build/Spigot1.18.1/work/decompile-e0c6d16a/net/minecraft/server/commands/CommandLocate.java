package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.EnumChatFormat;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;

public class CommandLocate {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.locate.failed"));

    public CommandLocate() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("locate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });

        Entry entry;

        for (Iterator iterator = StructureGenerator.STRUCTURES_REGISTRY.entrySet().iterator(); iterator.hasNext();literalargumentbuilder = (LiteralArgumentBuilder) literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal((String) entry.getKey()).executes((commandcontext) -> {
            return locate((CommandListenerWrapper) commandcontext.getSource(), (StructureGenerator) entry.getValue());
        }))) {
            entry = (Entry) iterator.next();
        }

        commanddispatcher.register(literalargumentbuilder);
    }

    private static int locate(CommandListenerWrapper commandlistenerwrapper, StructureGenerator<?> structuregenerator) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = commandlistenerwrapper.getLevel().findNearestMapFeature(structuregenerator, blockposition, 100, false);

        if (blockposition1 == null) {
            throw CommandLocate.ERROR_FAILED.create();
        } else {
            return showLocateResult(commandlistenerwrapper, structuregenerator.getFeatureName(), blockposition, blockposition1, "commands.locate.success");
        }
    }

    public static int showLocateResult(CommandListenerWrapper commandlistenerwrapper, String s, BlockPosition blockposition, BlockPosition blockposition1, String s1) {
        int i = MathHelper.floor(dist(blockposition.getX(), blockposition.getZ(), blockposition1.getX(), blockposition1.getZ()));
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.wrapInSquareBrackets(new ChatMessage("chat.coordinates", new Object[]{blockposition1.getX(), "~", blockposition1.getZ()})).withStyle((chatmodifier) -> {
            ChatModifier chatmodifier1 = chatmodifier.withColor(EnumChatFormat.GREEN);
            ChatClickable.EnumClickAction chatclickable_enumclickaction = ChatClickable.EnumClickAction.SUGGEST_COMMAND;
            int j = blockposition1.getX();

            return chatmodifier1.withClickEvent(new ChatClickable(chatclickable_enumclickaction, "/tp @s " + j + " ~ " + blockposition1.getZ())).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.coordinates.tooltip")));
        });

        commandlistenerwrapper.sendSuccess(new ChatMessage(s1, new Object[]{s, ichatmutablecomponent, i}), false);
        return i;
    }

    private static float dist(int i, int j, int k, int l) {
        int i1 = k - i;
        int j1 = l - j;

        return MathHelper.sqrt((float) (i1 * i1 + j1 * j1));
    }
}
