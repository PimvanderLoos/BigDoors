package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nullable;

public class CommandPardonIP extends CommandAbstract {

    public CommandPardonIP() {}

    public String getCommand() {
        return "pardon-ip";
    }

    public int a() {
        return 3;
    }

    public boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener) {
        return minecraftserver.getPlayerList().getIPBans().isEnabled() && super.canUse(minecraftserver, icommandlistener);
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.unbanip.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length == 1 && astring[0].length() > 1) {
            Matcher matcher = CommandBanIp.a.matcher(astring[0]);

            if (matcher.matches()) {
                minecraftserver.getPlayerList().getIPBans().remove(astring[0]);
                a(icommandlistener, (ICommand) this, "commands.unbanip.success", new Object[] { astring[0]});
            } else {
                throw new ExceptionInvalidSyntax("commands.unbanip.invalid", new Object[0]);
            }
        } else {
            throw new ExceptionUsage("commands.unbanip.usage", new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayerList().getIPBans().getEntries()) : Collections.emptyList();
    }
}
