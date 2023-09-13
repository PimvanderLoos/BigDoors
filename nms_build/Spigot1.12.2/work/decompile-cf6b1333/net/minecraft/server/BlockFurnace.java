package net.minecraft.server;

import java.util.Random;

public class BlockFurnace extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    private final boolean b;
    private static boolean c;

    protected BlockFurnace(boolean flag) {
        super(Material.STONE);
        this.w(this.blockStateList.getBlockData().set(BlockFurnace.FACING, EnumDirection.NORTH));
        this.b = flag;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.FURNACE);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            IBlockData iblockdata1 = world.getType(blockposition.north());
            IBlockData iblockdata2 = world.getType(blockposition.south());
            IBlockData iblockdata3 = world.getType(blockposition.west());
            IBlockData iblockdata4 = world.getType(blockposition.east());
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockFurnace.FACING);

            if (enumdirection == EnumDirection.NORTH && iblockdata1.b() && !iblockdata2.b()) {
                enumdirection = EnumDirection.SOUTH;
            } else if (enumdirection == EnumDirection.SOUTH && iblockdata2.b() && !iblockdata1.b()) {
                enumdirection = EnumDirection.NORTH;
            } else if (enumdirection == EnumDirection.WEST && iblockdata3.b() && !iblockdata4.b()) {
                enumdirection = EnumDirection.EAST;
            } else if (enumdirection == EnumDirection.EAST && iblockdata4.b() && !iblockdata3.b()) {
                enumdirection = EnumDirection.WEST;
            }

            world.setTypeAndData(blockposition, iblockdata.set(BlockFurnace.FACING, enumdirection), 2);
        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityFurnace) {
                entityhuman.openContainer((TileEntityFurnace) tileentity);
                entityhuman.b(StatisticList.Y);
            }

            return true;
        }
    }

    public static void a(boolean flag, World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);
        TileEntity tileentity = world.getTileEntity(blockposition);

        BlockFurnace.c = true;
        if (flag) {
            world.setTypeAndData(blockposition, Blocks.LIT_FURNACE.getBlockData().set(BlockFurnace.FACING, iblockdata.get(BlockFurnace.FACING)), 3);
            world.setTypeAndData(blockposition, Blocks.LIT_FURNACE.getBlockData().set(BlockFurnace.FACING, iblockdata.get(BlockFurnace.FACING)), 3);
        } else {
            world.setTypeAndData(blockposition, Blocks.FURNACE.getBlockData().set(BlockFurnace.FACING, iblockdata.get(BlockFurnace.FACING)), 3);
            world.setTypeAndData(blockposition, Blocks.FURNACE.getBlockData().set(BlockFurnace.FACING, iblockdata.get(BlockFurnace.FACING)), 3);
        }

        BlockFurnace.c = false;
        if (tileentity != null) {
            tileentity.A();
            world.setTileEntity(blockposition, tileentity);
        }

    }

    public TileEntity a(World world, int i) {
        return new TileEntityFurnace();
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockFurnace.FACING, entityliving.getDirection().opposite());
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setTypeAndData(blockposition, iblockdata.set(BlockFurnace.FACING, entityliving.getDirection().opposite()), 2);
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityFurnace) {
                ((TileEntityFurnace) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!BlockFurnace.c) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityFurnace) {
                InventoryUtils.dropInventory(world, blockposition, (TileEntityFurnace) tileentity);
                world.updateAdjacentComparators(blockposition, this);
            }
        }

        super.remove(world, blockposition, iblockdata);
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.FURNACE);
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection = EnumDirection.fromType1(i);

        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            enumdirection = EnumDirection.NORTH;
        }

        return this.getBlockData().set(BlockFurnace.FACING, enumdirection);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockFurnace.FACING)).a();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockFurnace.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockFurnace.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockFurnace.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFurnace.FACING});
    }
}
