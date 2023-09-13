package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.EnumGamemode;

public class CommandGamemodeDefault {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("defaultgamemode").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });
        EnumGamemode[] aenumgamemode = EnumGamemode.values();
        int i = aenumgamemode.length;

        for (int j = 0; j < i; ++j) {
            EnumGamemode enumgamemode = aenumgamemode[j];

            if (enumgamemode != EnumGamemode.NOT_SET) {
                literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.a(enumgamemode.b()).executes((commandcontext) -> {
                    return a((CommandListenerWrapper) commandcontext.getSource(), enumgamemode);
                }));
            }
        }

        commanddispatcher.register(literalargumentbuilder);
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, EnumGamemode enumgamemode) {
        int i = 0;
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        minecraftserver.a(enumgamemode);
        if (minecraftserver.getForceGamemode()) {
            Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer.playerInteractManager.getGameMode() != enumgamemode) {
                    entityplayer.a(enumgamemode);
                    ++i;
                }
            }
        }

        commandlistenerwrapper.sendMessage(new ChatMessage("commands.defaultgamemode.success", new Object[]{enumgamemode.c()}), true);
        return i;
    }
}
