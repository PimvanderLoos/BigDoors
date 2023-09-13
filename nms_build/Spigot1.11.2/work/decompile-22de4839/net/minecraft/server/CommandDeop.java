package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandDeop extends CommandAbstract {

    public CommandDeop() {}

    public String getCommand() {
        return "deop";
    }

    public int a() {
        return 3;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.deop.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length == 1 && astring[0].length() > 0) {
            GameProfile gameprofile = minecraftserver.getPlayerList().getOPs().a(astring[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.deop.failed", new Object[] { astring[0]});
            } else {
                minecraftserver.getPlayerList().removeOp(gameprofile);
                a(icommandlistener, (ICommand) this, "commands.deop.success", new Object[] { astring[0]});
            }
        } else {
            throw new ExceptionUsage("commands.deop.usage", new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayerList().n()) : Collections.emptyList();
    }
}
