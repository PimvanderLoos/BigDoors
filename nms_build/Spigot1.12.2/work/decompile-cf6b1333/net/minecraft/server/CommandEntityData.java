package net.minecraft.server;

import java.util.UUID;

public class CommandEntityData extends CommandAbstract {

    public CommandEntityData() {}

    public String getCommand() {
        return "entitydata";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.entitydata.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 2) {
            throw new ExceptionUsage("commands.entitydata.usage", new Object[0]);
        } else {
            Entity entity = c(minecraftserver, icommandlistener, astring[0]);

            if (entity instanceof EntityHuman) {
                throw new CommandException("commands.entitydata.noPlayers", new Object[] { entity.getScoreboardDisplayName()});
            } else {
                NBTTagCompound nbttagcompound = a(entity);
                NBTTagCompound nbttagcompound1 = nbttagcompound.g();

                NBTTagCompound nbttagcompound2;

                try {
                    nbttagcompound2 = MojangsonParser.parse(a(astring, 1));
                } catch (MojangsonParseException mojangsonparseexception) {
                    throw new CommandException("commands.entitydata.tagError", new Object[] { mojangsonparseexception.getMessage()});
                }

                UUID uuid = entity.getUniqueID();

                nbttagcompound.a(nbttagcompound2);
                entity.a(uuid);
                if (nbttagcompound.equals(nbttagcompound1)) {
                    throw new CommandException("commands.entitydata.failed", new Object[] { nbttagcompound.toString()});
                } else {
                    entity.f(nbttagcompound);
                    a(icommandlistener, (ICommand) this, "commands.entitydata.success", new Object[] { nbttagcompound.toString()});
                }
            }
        }
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }
}
