package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class CommandFill extends CommandAbstract {

    public CommandFill() {}

    public String getCommand() {
        return "fill";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.fill.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 7) {
            throw new ExceptionUsage("commands.fill.usage", new Object[0]);
        } else {
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 0);
            BlockPosition blockposition = a(icommandlistener, astring, 0, false);
            BlockPosition blockposition1 = a(icommandlistener, astring, 3, false);
            Block block = CommandAbstract.b(icommandlistener, astring[6]);
            IBlockData iblockdata;

            if (astring.length >= 8) {
                iblockdata = a(block, astring[7]);
            } else {
                iblockdata = block.getBlockData();
            }

            BlockPosition blockposition2 = new BlockPosition(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()));
            BlockPosition blockposition3 = new BlockPosition(Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
            int i = (blockposition3.getX() - blockposition2.getX() + 1) * (blockposition3.getY() - blockposition2.getY() + 1) * (blockposition3.getZ() - blockposition2.getZ() + 1);

            if (i > '\u8000') {
                throw new CommandException("commands.fill.tooManyBlocks", new Object[] { Integer.valueOf(i), Integer.valueOf('\u8000')});
            } else if (blockposition2.getY() >= 0 && blockposition3.getY() < 256) {
                World world = icommandlistener.getWorld();

                for (int j = blockposition2.getZ(); j <= blockposition3.getZ(); j += 16) {
                    for (int k = blockposition2.getX(); k <= blockposition3.getX(); k += 16) {
                        if (!world.isLoaded(new BlockPosition(k, blockposition3.getY() - blockposition2.getY(), j))) {
                            throw new CommandException("commands.fill.outOfWorld", new Object[0]);
                        }
                    }
                }

                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (astring.length >= 10 && block.isTileEntity()) {
                    String s = a(astring, 9);

                    try {
                        nbttagcompound = MojangsonParser.parse(s);
                        flag = true;
                    } catch (MojangsonParseException mojangsonparseexception) {
                        throw new CommandException("commands.fill.tagError", new Object[] { mojangsonparseexception.getMessage()});
                    }
                }

                ArrayList arraylist = Lists.newArrayList();

                i = 0;

                for (int l = blockposition2.getZ(); l <= blockposition3.getZ(); ++l) {
                    for (int i1 = blockposition2.getY(); i1 <= blockposition3.getY(); ++i1) {
                        for (int j1 = blockposition2.getX(); j1 <= blockposition3.getX(); ++j1) {
                            BlockPosition blockposition4 = new BlockPosition(j1, i1, l);

                            if (astring.length >= 9) {
                                if (!"outline".equals(astring[8]) && !"hollow".equals(astring[8])) {
                                    if ("destroy".equals(astring[8])) {
                                        world.setAir(blockposition4, true);
                                    } else if ("keep".equals(astring[8])) {
                                        if (!world.isEmpty(blockposition4)) {
                                            continue;
                                        }
                                    } else if ("replace".equals(astring[8]) && !block.isTileEntity() && astring.length > 9) {
                                        Block block1 = CommandAbstract.b(icommandlistener, astring[9]);

                                        if (world.getType(blockposition4).getBlock() != block1 || astring.length > 10 && !"-1".equals(astring[10]) && !"*".equals(astring[10]) && !CommandAbstract.b(block1, astring[10]).apply(world.getType(blockposition4))) {
                                            continue;
                                        }
                                    }
                                } else if (j1 != blockposition2.getX() && j1 != blockposition3.getX() && i1 != blockposition2.getY() && i1 != blockposition3.getY() && l != blockposition2.getZ() && l != blockposition3.getZ()) {
                                    if ("hollow".equals(astring[8])) {
                                        world.setTypeAndData(blockposition4, Blocks.AIR.getBlockData(), 2);
                                        arraylist.add(blockposition4);
                                    }
                                    continue;
                                }
                            }

                            TileEntity tileentity = world.getTileEntity(blockposition4);

                            if (tileentity != null && tileentity instanceof IInventory) {
                                ((IInventory) tileentity).clear();
                            }

                            if (world.setTypeAndData(blockposition4, iblockdata, 2)) {
                                arraylist.add(blockposition4);
                                ++i;
                                if (flag) {
                                    TileEntity tileentity1 = world.getTileEntity(blockposition4);

                                    if (tileentity1 != null) {
                                        nbttagcompound.setInt("x", blockposition4.getX());
                                        nbttagcompound.setInt("y", blockposition4.getY());
                                        nbttagcompound.setInt("z", blockposition4.getZ());
                                        tileentity1.load(nbttagcompound);
                                    }
                                }
                            }
                        }
                    }
                }

                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition5 = (BlockPosition) iterator.next();
                    Block block2 = world.getType(blockposition5).getBlock();

                    world.update(blockposition5, block2, false);
                }

                if (i <= 0) {
                    throw new CommandException("commands.fill.failed", new Object[0]);
                } else {
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, i);
                    a(icommandlistener, (ICommand) this, "commands.fill.success", new Object[] { Integer.valueOf(i)});
                }
            } else {
                throw new CommandException("commands.fill.outOfWorld", new Object[0]);
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length > 0 && astring.length <= 3 ? a(astring, 0, blockposition) : (astring.length > 3 && astring.length <= 6 ? a(astring, 3, blockposition) : (astring.length == 7 ? a(astring, (Collection) Block.REGISTRY.keySet()) : (astring.length == 9 ? a(astring, new String[] { "replace", "destroy", "keep", "hollow", "outline"}) : (astring.length == 10 && "replace".equals(astring[8]) ? a(astring, (Collection) Block.REGISTRY.keySet()) : Collections.emptyList()))));
    }
}
