package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;

public class CommandGamemode {

    public static final int PERMISSION_LEVEL = 2;

    public CommandGamemode() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("gamemode").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });
        EnumGamemode[] aenumgamemode = EnumGamemode.values();
        int i = aenumgamemode.length;

        for (int j = 0; j < i; ++j) {
            EnumGamemode enumgamemode = aenumgamemode[j];

            literalargumentbuilder.then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal(enumgamemode.getName()).executes((commandcontext) -> {
                return setMode(commandcontext, Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException()), enumgamemode);
            })).then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentEntity.players()).executes((commandcontext) -> {
                return setMode(commandcontext, ArgumentEntity.getPlayers(commandcontext, "target"), enumgamemode);
            })));
        }

        commanddispatcher.register(literalargumentbuilder);
    }

    private static void logGamemodeChange(CommandListenerWrapper commandlistenerwrapper, EntityPlayer entityplayer, EnumGamemode enumgamemode) {
        ChatMessage chatmessage = new ChatMessage("gameMode." + enumgamemode.getName());

        if (commandlistenerwrapper.getEntity() == entityplayer) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.gamemode.success.self", new Object[]{chatmessage}), true);
        } else {
            if (commandlistenerwrapper.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
                entityplayer.sendMessage(new ChatMessage("gameMode.changed", new Object[]{chatmessage}), SystemUtils.NIL_UUID);
            }

            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.gamemode.success.other", new Object[]{entityplayer.getDisplayName(), chatmessage}), true);
        }

    }

    private static int setMode(CommandContext<CommandListenerWrapper> commandcontext, Collection<EntityPlayer> collection, EnumGamemode enumgamemode) {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.setGameMode(enumgamemode)) {
                logGamemodeChange((CommandListenerWrapper) commandcontext.getSource(), entityplayer, enumgamemode);
                ++i;
            }
        }

        return i;
    }
}
