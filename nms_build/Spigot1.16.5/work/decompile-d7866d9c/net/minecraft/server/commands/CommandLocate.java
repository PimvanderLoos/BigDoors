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
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;

public class CommandLocate {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.locate.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("locate").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });

        Entry entry;

        for (Iterator iterator = StructureGenerator.a.entrySet().iterator(); iterator.hasNext();literalargumentbuilder = (LiteralArgumentBuilder) literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.a((String) entry.getKey()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), (StructureGenerator) entry.getValue());
        }))) {
            entry = (Entry) iterator.next();
        }

        commanddispatcher.register(literalargumentbuilder);
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, StructureGenerator<?> structuregenerator) throws CommandSyntaxException {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = commandlistenerwrapper.getWorld().a(structuregenerator, blockposition, 100, false);

        if (blockposition1 == null) {
            throw CommandLocate.a.create();
        } else {
            return a(commandlistenerwrapper, structuregenerator.i(), blockposition, blockposition1, "commands.locate.success");
        }
    }

    public static int a(CommandListenerWrapper commandlistenerwrapper, String s, BlockPosition blockposition, BlockPosition blockposition1, String s1) {
        int i = MathHelper.d(a(blockposition.getX(), blockposition.getZ(), blockposition1.getX(), blockposition1.getZ()));
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.a((IChatBaseComponent) (new ChatMessage("chat.coordinates", new Object[]{blockposition1.getX(), "~", blockposition1.getZ()}))).format((chatmodifier) -> {
            return chatmodifier.setColor(EnumChatFormat.GREEN).setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/tp @s " + blockposition1.getX() + " ~ " + blockposition1.getZ())).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage("chat.coordinates.tooltip")));
        });

        commandlistenerwrapper.sendMessage(new ChatMessage(s1, new Object[]{s, ichatmutablecomponent, i}), false);
        return i;
    }

    private static float a(int i, int j, int k, int l) {
        int i1 = k - i;
        int j1 = l - j;

        return MathHelper.c((float) (i1 * i1 + j1 * j1));
    }
}
