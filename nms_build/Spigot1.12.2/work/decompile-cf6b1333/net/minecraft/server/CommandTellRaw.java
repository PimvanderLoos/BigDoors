package net.minecraft.server;

import com.google.gson.JsonParseException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandTellRaw extends CommandAbstract {

    public CommandTellRaw() {}

    public String getCommand() {
        return "tellraw";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.tellraw.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 2) {
            throw new ExceptionUsage("commands.tellraw.usage", new Object[0]);
        } else {
            EntityPlayer entityplayer = b(minecraftserver, icommandlistener, astring[0]);
            String s = a(astring, 1);

            try {
                IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);

                entityplayer.sendMessage(ChatComponentUtils.filterForDisplay(icommandlistener, ichatbasecomponent, entityplayer));
            } catch (JsonParseException jsonparseexception) {
                throw a(jsonparseexception);
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList();
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
