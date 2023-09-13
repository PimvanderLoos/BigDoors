package net.minecraft.server;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class CommandBanIp extends CommandAbstract {

    public static final Pattern a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public CommandBanIp() {}

    public String getCommand() {
        return "ban-ip";
    }

    public int a() {
        return 3;
    }

    public boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener) {
        return minecraftserver.getPlayerList().getIPBans().isEnabled() && super.canUse(minecraftserver, icommandlistener);
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.banip.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length >= 1 && astring[0].length() > 1) {
            IChatBaseComponent ichatbasecomponent = astring.length >= 2 ? a(icommandlistener, astring, 1) : null;
            Matcher matcher = CommandBanIp.a.matcher(astring[0]);

            if (matcher.matches()) {
                this.a(minecraftserver, icommandlistener, astring[0], ichatbasecomponent == null ? null : ichatbasecomponent.toPlainText());
            } else {
                EntityPlayer entityplayer = minecraftserver.getPlayerList().getPlayer(astring[0]);

                if (entityplayer == null) {
                    throw new ExceptionPlayerNotFound("commands.banip.invalid");
                }

                this.a(minecraftserver, icommandlistener, entityplayer.A(), ichatbasecomponent == null ? null : ichatbasecomponent.toPlainText());
            }

        } else {
            throw new ExceptionUsage("commands.banip.usage", new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList();
    }

    protected void a(MinecraftServer minecraftserver, ICommandListener icommandlistener, String s, @Nullable String s1) {
        IpBanEntry ipbanentry = new IpBanEntry(s, (Date) null, icommandlistener.getName(), (Date) null, s1);

        minecraftserver.getPlayerList().getIPBans().add(ipbanentry);
        List list = minecraftserver.getPlayerList().b(s);
        String[] astring = new String[list.size()];
        int i = 0;

        EntityPlayer entityplayer;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); astring[i++] = entityplayer.getName()) {
            entityplayer = (EntityPlayer) iterator.next();
            entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.ip_banned", new Object[0]));
        }

        if (list.isEmpty()) {
            a(icommandlistener, (ICommand) this, "commands.banip.success", new Object[] { s});
        } else {
            a(icommandlistener, (ICommand) this, "commands.banip.success.players", new Object[] { s, a((Object[]) astring)});
        }

    }
}
