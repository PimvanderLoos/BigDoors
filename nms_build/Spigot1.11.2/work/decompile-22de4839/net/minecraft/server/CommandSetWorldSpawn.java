package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandSetWorldSpawn extends CommandAbstract {

    public CommandSetWorldSpawn() {}

    public String getCommand() {
        return "setworldspawn";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.setworldspawn.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        BlockPosition blockposition;

        if (astring.length == 0) {
            blockposition = a(icommandlistener).getChunkCoordinates();
        } else {
            if (astring.length != 3 || icommandlistener.getWorld() == null) {
                throw new ExceptionUsage("commands.setworldspawn.usage", new Object[0]);
            }

            blockposition = a(icommandlistener, astring, 0, true);
        }

        icommandlistener.getWorld().A(blockposition);
        minecraftserver.getPlayerList().sendAll(new PacketPlayOutSpawnPosition(blockposition));
        a(icommandlistener, (ICommand) this, "commands.setworldspawn.success", new Object[] { Integer.valueOf(blockposition.getX()), Integer.valueOf(blockposition.getY()), Integer.valueOf(blockposition.getZ())});
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length > 0 && astring.length <= 3 ? a(astring, 0, blockposition) : Collections.emptyList();
    }
}
