package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandSpawnpoint extends CommandAbstract {

    public CommandSpawnpoint() {}

    public String getCommand() {
        return "spawnpoint";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.spawnpoint.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length > 1 && astring.length < 4) {
            throw new ExceptionUsage("commands.spawnpoint.usage", new Object[0]);
        } else {
            EntityPlayer entityplayer = astring.length > 0 ? b(minecraftserver, icommandlistener, astring[0]) : a(icommandlistener);
            BlockPosition blockposition = astring.length > 3 ? a(icommandlistener, astring, 1, true) : entityplayer.getChunkCoordinates();

            if (entityplayer.world != null) {
                entityplayer.setRespawnPosition(blockposition, true);
                a(icommandlistener, (ICommand) this, "commands.spawnpoint.success", new Object[] { entityplayer.getName(), Integer.valueOf(blockposition.getX()), Integer.valueOf(blockposition.getY()), Integer.valueOf(blockposition.getZ())});
            }

        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : (astring.length > 1 && astring.length <= 4 ? a(astring, 1, blockposition) : Collections.emptyList());
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
