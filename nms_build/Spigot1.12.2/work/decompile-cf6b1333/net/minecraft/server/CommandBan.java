package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

public class CommandBan extends CommandAbstract {

    public CommandBan() {}

    public String getCommand() {
        return "ban";
    }

    public int a() {
        return 3;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.ban.usage";
    }

    public boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener) {
        return minecraftserver.getPlayerList().getProfileBans().isEnabled() && super.canUse(minecraftserver, icommandlistener);
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length >= 1 && astring[0].length() > 0) {
            GameProfile gameprofile = minecraftserver.getUserCache().getProfile(astring[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.ban.failed", new Object[] { astring[0]});
            } else {
                String s = null;

                if (astring.length >= 2) {
                    s = a(icommandlistener, astring, 1).toPlainText();
                }

                GameProfileBanEntry gameprofilebanentry = new GameProfileBanEntry(gameprofile, (Date) null, icommandlistener.getName(), (Date) null, s);

                minecraftserver.getPlayerList().getProfileBans().add(gameprofilebanentry);
                EntityPlayer entityplayer = minecraftserver.getPlayerList().getPlayer(astring[0]);

                if (entityplayer != null) {
                    entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.banned", new Object[0]));
                }

                a(icommandlistener, (ICommand) this, "commands.ban.success", new Object[] { astring[0]});
            }
        } else {
            throw new ExceptionUsage("commands.ban.usage", new Object[0]);
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length >= 1 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList();
    }
}
