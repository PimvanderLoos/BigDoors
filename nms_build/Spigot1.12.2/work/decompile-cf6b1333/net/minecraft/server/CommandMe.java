package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public class CommandMe extends CommandAbstract {

    public CommandMe() {}

    public String getCommand() {
        return "me";
    }

    public int a() {
        return 0;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.me.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length <= 0) {
            throw new ExceptionUsage("commands.me.usage", new Object[0]);
        } else {
            IChatBaseComponent ichatbasecomponent = b(icommandlistener, astring, 0, !(icommandlistener instanceof EntityHuman));

            minecraftserver.getPlayerList().sendMessage(new ChatMessage("chat.type.emote", new Object[] { icommandlistener.getScoreboardDisplayName(), ichatbasecomponent}));
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return a(astring, minecraftserver.getPlayers());
    }
}
