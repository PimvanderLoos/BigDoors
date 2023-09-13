package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandTestFor extends CommandAbstract {

    public CommandTestFor() {}

    public String getCommand() {
        return "testfor";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.testfor.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 1) {
            throw new ExceptionUsage("commands.testfor.usage", new Object[0]);
        } else {
            Entity entity = c(minecraftserver, icommandlistener, astring[0]);
            NBTTagCompound nbttagcompound = null;

            if (astring.length >= 2) {
                try {
                    nbttagcompound = MojangsonParser.parse(a(astring, 1));
                } catch (MojangsonParseException mojangsonparseexception) {
                    throw new CommandException("commands.testfor.tagError", new Object[] { mojangsonparseexception.getMessage()});
                }
            }

            if (nbttagcompound != null) {
                NBTTagCompound nbttagcompound1 = a(entity);

                if (!GameProfileSerializer.a(nbttagcompound, nbttagcompound1, true)) {
                    throw new CommandException("commands.testfor.failure", new Object[] { entity.getName()});
                }
            }

            a(icommandlistener, (ICommand) this, "commands.testfor.success", new Object[] { entity.getName()});
        }
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList();
    }
}
