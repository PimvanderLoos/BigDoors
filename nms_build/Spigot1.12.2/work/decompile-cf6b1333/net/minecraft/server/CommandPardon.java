package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandPardon extends CommandAbstract {

    public CommandPardon() {}

    public String getCommand() {
        return "pardon";
    }

    public int a() {
        return 3;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.unban.usage";
    }

    public boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener) {
        return minecraftserver.getPlayerList().getProfileBans().isEnabled() && super.canUse(minecraftserver, icommandlistener);
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length == 1 && astring[0].length() > 0) {
            GameProfile gameprofile = minecraftserver.getPlayerList().getProfileBans().a(astring[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.unban.failed", new Object[] { astring[0]});
            } else {
                minecraftserver.getPlayerList().getProfileBans().remove(gameprofile);
                a(icommandlistener, (ICommand) this, "commands.unban.success", new Object[] { astring[0]});
            }
        } else {
            throw new ExceptionUsage("commands.unban.usage", new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayerList().getProfileBans().getEntries()) : Collections.emptyList();
    }
}
