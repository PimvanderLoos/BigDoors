package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandBanList extends CommandAbstract {

    public CommandBanList() {}

    public String getCommand() {
        return "banlist";
    }

    public int a() {
        return 3;
    }

    public boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener) {
        return (minecraftserver.getPlayerList().getIPBans().isEnabled() || minecraftserver.getPlayerList().getProfileBans().isEnabled()) && super.canUse(minecraftserver, icommandlistener);
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.banlist.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length >= 1 && "ips".equalsIgnoreCase(astring[0])) {
            icommandlistener.sendMessage(new ChatMessage("commands.banlist.ips", new Object[] { Integer.valueOf(minecraftserver.getPlayerList().getIPBans().getEntries().length)}));
            icommandlistener.sendMessage(new ChatComponentText(a((Object[]) minecraftserver.getPlayerList().getIPBans().getEntries())));
        } else {
            icommandlistener.sendMessage(new ChatMessage("commands.banlist.players", new Object[] { Integer.valueOf(minecraftserver.getPlayerList().getProfileBans().getEntries().length)}));
            icommandlistener.sendMessage(new ChatComponentText(a((Object[]) minecraftserver.getPlayerList().getProfileBans().getEntries())));
        }

    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "players", "ips"}) : Collections.emptyList();
    }
}
