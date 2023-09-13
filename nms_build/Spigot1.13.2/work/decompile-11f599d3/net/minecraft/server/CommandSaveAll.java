package net.minecraft.server;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;

public class CommandSaveAll {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.save.failed", new Object[0]));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("save-all").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(4);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), false);
        })).then(CommandDispatcher.a("flush").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), true);
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, boolean flag) throws CommandSyntaxException {
        commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.saving", new Object[0]), false);
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        boolean flag1 = false;

        minecraftserver.getPlayerList().savePlayers();
        Iterator iterator = minecraftserver.getWorlds().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver != null && a(worldserver, flag)) {
                flag1 = true;
            }
        }

        if (!flag1) {
            throw CommandSaveAll.a.create();
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.save.success", new Object[0]), true);
            return 1;
        }
    }

    private static boolean a(WorldServer worldserver, boolean flag) {
        boolean flag1 = worldserver.savingDisabled;

        worldserver.savingDisabled = false;

        boolean flag2;

        try {
            worldserver.save(true, (IProgressUpdate) null);
            if (flag) {
                worldserver.flushSave();
            }

            return true;
        } catch (ExceptionWorldConflict exceptionworldconflict) {
            flag2 = false;
        } finally {
            worldserver.savingDisabled = flag1;
        }

        return flag2;
    }
}
