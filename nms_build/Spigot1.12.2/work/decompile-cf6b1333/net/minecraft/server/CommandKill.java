package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandKill extends CommandAbstract {

    public CommandKill() {}

    public String getCommand() {
        return "kill";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.kill.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length == 0) {
            EntityPlayer entityplayer = a(icommandlistener);

            entityplayer.killEntity();
            a(icommandlistener, (ICommand) this, "commands.kill.successful", new Object[] { entityplayer.getScoreboardDisplayName()});
        } else {
            Entity entity = c(minecraftserver, icommandlistener, astring[0]);

            entity.killEntity();
            a(icommandlistener, (ICommand) this, "commands.kill.successful", new Object[] { entity.getScoreboardDisplayName()});
        }
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList();
    }
}
