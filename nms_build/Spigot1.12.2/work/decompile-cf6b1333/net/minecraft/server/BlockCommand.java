package net.minecraft.server;

import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockCommand extends BlockTileEntity {

    private static final Logger c = LogManager.getLogger();
    public static final BlockStateDirection a = BlockDirectional.FACING;
    public static final BlockStateBoolean b = BlockStateBoolean.of("conditional");

    public BlockCommand(MaterialMapColor materialmapcolor) {
        super(Material.ORE, materialmapcolor);
        this.w(this.blockStateList.getBlockData().set(BlockCommand.a, EnumDirection.NORTH).set(BlockCommand.b, Boolean.valueOf(false)));
    }

    public TileEntity a(World world, int i) {
        TileEntityCommand tileentitycommand = new TileEntityCommand();

        tileentitycommand.b(this == Blocks.dd);
        return tileentitycommand;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
                boolean flag = world.isBlockIndirectlyPowered(blockposition);
                boolean flag1 = tileentitycommand.f();

                tileentitycommand.a(flag);
                if (!flag1 && !tileentitycommand.h() && tileentitycommand.l() != TileEntityCommand.Type.SEQUENCE) {
                    if (flag) {
                        tileentitycommand.j();
                        world.a(blockposition, (Block) this, this.a(world));
                    }

                }
            }
        }
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
                CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();
                boolean flag = !UtilColor.b(commandblocklistenerabstract.getCommand());
                TileEntityCommand.Type tileentitycommand_type = tileentitycommand.l();
                boolean flag1 = tileentitycommand.i();

                if (tileentitycommand_type == TileEntityCommand.Type.AUTO) {
                    tileentitycommand.j();
                    if (flag1) {
                        this.a(iblockdata, world, blockposition, commandblocklistenerabstract, flag);
                    } else if (tileentitycommand.m()) {
                        commandblocklistenerabstract.a(0);
                    }

                    if (tileentitycommand.f() || tileentitycommand.h()) {
                        world.a(blockposition, (Block) this, this.a(world));
                    }
                } else if (tileentitycommand_type == TileEntityCommand.Type.REDSTONE) {
                    if (flag1) {
                        this.a(iblockdata, world, blockposition, commandblocklistenerabstract, flag);
                    } else if (tileentitycommand.m()) {
                        commandblocklistenerabstract.a(0);
                    }
                }

                world.updateAdjacentComparators(blockposition, this);
            }

        }
    }

    private void a(IBlockData iblockdata, World world, BlockPosition blockposition, CommandBlockListenerAbstract commandblocklistenerabstract, boolean flag) {
        if (flag) {
            commandblocklistenerabstract.a(world);
        } else {
            commandblocklistenerabstract.a(0);
        }

        c(world, blockposition, (EnumDirection) iblockdata.get(BlockCommand.a));
    }

    public int a(World world) {
        return 1;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCommand && entityhuman.isCreativeAndOp()) {
            entityhuman.a((TileEntityCommand) tileentity);
            return true;
        } else {
            return false;
        }
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityCommand ? ((TileEntityCommand) tileentity).getCommandBlock().k() : 0;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCommand) {
            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
            CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();

            if (itemstack.hasName()) {
                commandblocklistenerabstract.setName(itemstack.getName());
            }

            if (!world.isClientSide) {
                NBTTagCompound nbttagcompound = itemstack.getTag();

                if (nbttagcompound == null || !nbttagcompound.hasKeyOfType("BlockEntityTag", 10)) {
                    commandblocklistenerabstract.a(world.getGameRules().getBoolean("sendCommandFeedback"));
                    tileentitycommand.b(this == Blocks.dd);
                }

                if (tileentitycommand.l() == TileEntityCommand.Type.SEQUENCE) {
                    boolean flag = world.isBlockIndirectlyPowered(blockposition);

                    tileentitycommand.a(flag);
                }
            }

        }
    }

    public int a(Random random) {
        return 0;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCommand.a, EnumDirection.fromType1(i & 7)).set(BlockCommand.b, Boolean.valueOf((i & 8) != 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockCommand.a)).a() | (((Boolean) iblockdata.get(BlockCommand.b)).booleanValue() ? 8 : 0);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockCommand.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockCommand.a)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockCommand.a)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockCommand.a, BlockCommand.b});
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockCommand.a, EnumDirection.a(blockposition, entityliving)).set(BlockCommand.b, Boolean.valueOf(false));
    }

    private static void c(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);
        GameRules gamerules = world.getGameRules();

        int i;
        IBlockData iblockdata;

        for (i = gamerules.c("maxCommandChainLength"); i-- > 0; enumdirection = (EnumDirection) iblockdata.get(BlockCommand.a)) {
            blockposition_mutableblockposition.c(enumdirection);
            iblockdata = world.getType(blockposition_mutableblockposition);
            Block block = iblockdata.getBlock();

            if (block != Blocks.dd) {
                break;
            }

            TileEntity tileentity = world.getTileEntity(blockposition_mutableblockposition);

            if (!(tileentity instanceof TileEntityCommand)) {
                break;
            }

            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;

            if (tileentitycommand.l() != TileEntityCommand.Type.SEQUENCE) {
                break;
            }

            if (tileentitycommand.f() || tileentitycommand.h()) {
                CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();

                if (tileentitycommand.j()) {
                    if (!commandblocklistenerabstract.a(world)) {
                        break;
                    }

                    world.updateAdjacentComparators(blockposition_mutableblockposition, block);
                } else if (tileentitycommand.m()) {
                    commandblocklistenerabstract.a(0);
                }
            }
        }

        if (i <= 0) {
            int j = Math.max(gamerules.c("maxCommandChainLength"), 0);

            BlockCommand.c.warn("Commandblock chain tried to execure more than " + j + " steps!");
        }

    }
}
