package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandSaveAll extends CommandAbstract {

    public CommandSaveAll() {}

    public String getCommand() {
        return "save-all";
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.save.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        icommandlistener.sendMessage(new ChatMessage("commands.save.start", new Object[0]));
        if (minecraftserver.getPlayerList() != null) {
            minecraftserver.getPlayerList().savePlayers();
        }

        try {
            int i;
            WorldServer worldserver;
            boolean flag;

            for (i = 0; i < minecraftserver.worldServer.length; ++i) {
                if (minecraftserver.worldServer[i] != null) {
                    worldserver = minecraftserver.worldServer[i];
                    flag = worldserver.savingDisabled;
                    worldserver.savingDisabled = false;
                    worldserver.save(true, (IProgressUpdate) null);
                    worldserver.savingDisabled = flag;
                }
            }

            if (astring.length > 0 && "flush".equals(astring[0])) {
                icommandlistener.sendMessage(new ChatMessage("commands.save.flushStart", new Object[0]));

                for (i = 0; i < minecraftserver.worldServer.length; ++i) {
                    if (minecraftserver.worldServer[i] != null) {
                        worldserver = minecraftserver.worldServer[i];
                        flag = worldserver.savingDisabled;
                        worldserver.savingDisabled = false;
                        worldserver.flushSave();
                        worldserver.savingDisabled = flag;
                    }
                }

                icommandlistener.sendMessage(new ChatMessage("commands.save.flushEnd", new Object[0]));
            }
        } catch (ExceptionWorldConflict exceptionworldconflict) {
            a(icommandlistener, (ICommand) this, "commands.save.failed", new Object[] { exceptionworldconflict.getMessage()});
            return;
        }

        a(icommandlistener, (ICommand) this, "commands.save.success", new Object[0]);
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "flush"}) : Collections.emptyList();
    }
}
