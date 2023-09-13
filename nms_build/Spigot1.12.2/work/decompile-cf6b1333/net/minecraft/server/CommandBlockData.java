package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandBlockData extends CommandAbstract {

    public CommandBlockData() {}

    public String getCommand() {
        return "blockdata";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.blockdata.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 4) {
            throw new ExceptionUsage("commands.blockdata.usage", new Object[0]);
        } else {
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 0);
            BlockPosition blockposition = a(icommandlistener, astring, 0, false);
            World world = icommandlistener.getWorld();

            if (!world.isLoaded(blockposition)) {
                throw new CommandException("commands.blockdata.outOfWorld", new Object[0]);
            } else {
                IBlockData iblockdata = world.getType(blockposition);
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity == null) {
                    throw new CommandException("commands.blockdata.notValid", new Object[0]);
                } else {
                    NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());
                    NBTTagCompound nbttagcompound1 = nbttagcompound.g();

                    NBTTagCompound nbttagcompound2;

                    try {
                        nbttagcompound2 = MojangsonParser.parse(a(astring, 3));
                    } catch (MojangsonParseException mojangsonparseexception) {
                        throw new CommandException("commands.blockdata.tagError", new Object[] { mojangsonparseexception.getMessage()});
                    }

                    nbttagcompound.a(nbttagcompound2);
                    nbttagcompound.setInt("x", blockposition.getX());
                    nbttagcompound.setInt("y", blockposition.getY());
                    nbttagcompound.setInt("z", blockposition.getZ());
                    if (nbttagcompound.equals(nbttagcompound1)) {
                        throw new CommandException("commands.blockdata.failed", new Object[] { nbttagcompound.toString()});
                    } else {
                        tileentity.load(nbttagcompound);
                        tileentity.update();
                        world.notify(blockposition, iblockdata, iblockdata, 3);
                        icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 1);
                        a(icommandlistener, (ICommand) this, "commands.blockdata.success", new Object[] { nbttagcompound.toString()});
                    }
                }
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length > 0 && astring.length <= 3 ? a(astring, 0, blockposition) : Collections.emptyList();
    }
}
