package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandLocate extends CommandAbstract {

    public CommandLocate() {}

    public String getCommand() {
        return "locate";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.locate.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length != 1) {
            throw new ExceptionUsage("commands.locate.usage", new Object[0]);
        } else {
            String s = astring[0];
            BlockPosition blockposition = icommandlistener.getWorld().a(s, icommandlistener.getChunkCoordinates(), false);

            if (blockposition != null) {
                icommandlistener.sendMessage(new ChatMessage("commands.locate.success", new Object[] { s, Integer.valueOf(blockposition.getX()), Integer.valueOf(blockposition.getZ())}));
            } else {
                throw new CommandException("commands.locate.failure", new Object[] { s});
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "Stronghold", "Monument", "Village", "Mansion", "EndCity", "Fortress", "Temple", "Mineshaft"}) : Collections.emptyList();
    }
}
