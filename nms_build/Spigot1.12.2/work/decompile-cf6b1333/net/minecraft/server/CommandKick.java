package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandKick extends CommandAbstract {

    public CommandKick() {}

    public String getCommand() {
        return "kick";
    }

    public int a() {
        return 3;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.kick.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length > 0 && astring[0].length() > 1) {
            EntityPlayer entityplayer = minecraftserver.getPlayerList().getPlayer(astring[0]);

            if (entityplayer == null) {
                throw new ExceptionPlayerNotFound("commands.generic.player.notFound", new Object[] { astring[0]});
            } else {
                if (astring.length >= 2) {
                    IChatBaseComponent ichatbasecomponent = a(icommandlistener, astring, 1);

                    entityplayer.playerConnection.disconnect(ichatbasecomponent);
                    a(icommandlistener, (ICommand) this, "commands.kick.success.reason", new Object[] { entityplayer.getName(), ichatbasecomponent.toPlainText()});
                } else {
                    entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.kicked", new Object[0]));
                    a(icommandlistener, (ICommand) this, "commands.kick.success", new Object[] { entityplayer.getName()});
                }

            }
        } else {
            throw new ExceptionUsage("commands.kick.usage", new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length >= 1 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList();
    }
}
