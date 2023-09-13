package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty {

    private static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.difficulty.failure", new Object[]{object});
    });

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = net.minecraft.commands.CommandDispatcher.a("difficulty");
        EnumDifficulty[] aenumdifficulty = EnumDifficulty.values();
        int i = aenumdifficulty.length;

        for (int j = 0; j < i; ++j) {
            EnumDifficulty enumdifficulty = aenumdifficulty[j];

            literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.a(enumdifficulty.c()).executes((commandcontext) -> {
                return a((CommandListenerWrapper) commandcontext.getSource(), enumdifficulty);
            }));
        }

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) literalargumentbuilder.requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            EnumDifficulty enumdifficulty1 = ((CommandListenerWrapper) commandcontext.getSource()).getWorld().getDifficulty();

            ((CommandListenerWrapper) commandcontext.getSource()).sendMessage(new ChatMessage("commands.difficulty.query", new Object[]{enumdifficulty1.b()}), false);
            return enumdifficulty1.a();
        }));
    }

    public static int a(CommandListenerWrapper commandlistenerwrapper, EnumDifficulty enumdifficulty) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        if (minecraftserver.getSaveData().getDifficulty() == enumdifficulty) {
            throw CommandDifficulty.a.create(enumdifficulty.c());
        } else {
            minecraftserver.a(enumdifficulty, true);
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.difficulty.success", new Object[]{enumdifficulty.b()}), true);
            return 0;
        }
    }
}
