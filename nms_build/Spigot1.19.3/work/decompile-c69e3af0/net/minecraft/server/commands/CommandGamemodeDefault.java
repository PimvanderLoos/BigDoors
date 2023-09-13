package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.EnumGamemode;

public class CommandGamemodeDefault {

    public CommandGamemodeDefault() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("defaultgamemode").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("gamemode", GameModeArgument.gameMode()).executes((commandcontext) -> {
            return setMode((CommandListenerWrapper) commandcontext.getSource(), GameModeArgument.getGameMode(commandcontext, "gamemode"));
        })));
    }

    private static int setMode(CommandListenerWrapper commandlistenerwrapper, EnumGamemode enumgamemode) {
        int i = 0;
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();

        minecraftserver.setDefaultGameType(enumgamemode);
        EnumGamemode enumgamemode1 = minecraftserver.getForcedGameType();

        if (enumgamemode1 != null) {
            Iterator iterator = minecraftserver.getPlayerList().getPlayers().iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                if (entityplayer.setGameMode(enumgamemode1)) {
                    ++i;
                }
            }
        }

        commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.defaultgamemode.success", enumgamemode.getLongDisplayName()), true);
        return i;
    }
}
