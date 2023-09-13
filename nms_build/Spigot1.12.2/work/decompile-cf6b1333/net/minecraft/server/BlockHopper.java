package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;

public class BlockHopper extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", new Predicate() {
        public boolean a(@Nullable EnumDirection enumdirection) {
            return enumdirection != EnumDirection.UP;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EnumDirection) object);
        }
    });
    public static final BlockStateBoolean ENABLED = BlockStateBoolean.of("enabled");
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    public BlockHopper() {
        super(Material.ORE, MaterialMapColor.n);
        this.w(this.blockStateList.getBlockData().set(BlockHopper.FACING, EnumDirection.DOWN).set(BlockHopper.ENABLED, Boolean.valueOf(true)));
        this.a(CreativeModeTab.d);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockHopper.j;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        a(blockposition, axisalignedbb, list, BlockHopper.c);
        a(blockposition, axisalignedbb, list, BlockHopper.g);
        a(blockposition, axisalignedbb, list, BlockHopper.f);
        a(blockposition, axisalignedbb, list, BlockHopper.d);
        a(blockposition, axisalignedbb, list, BlockHopper.e);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        EnumDirection enumdirection1 = enumdirection.opposite();

        if (enumdirection1 == EnumDirection.UP) {
            enumdirection1 = EnumDirection.DOWN;
        }

        return this.getBlockData().set(BlockHopper.FACING, enumdirection1).set(BlockHopper.ENABLED, Boolean.valueOf(true));
    }

    public TileEntity a(World world, int i) {
        return new TileEntityHopper();
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                ((TileEntityHopper) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public boolean k(IBlockData iblockdata) {
        return true;
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                entityhuman.openContainer((TileEntityHopper) tileentity);
                entityhuman.b(StatisticList.P);
            }

            return true;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.e(world, blockposition, iblockdata);
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = !world.isBlockIndirectlyPowered(blockposition);

        if (flag != ((Boolean) iblockdata.get(BlockHopper.ENABLED)).booleanValue()) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockHopper.ENABLED, Boolean.valueOf(flag)), 4);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityHopper) {
            InventoryUtils.dropInventory(world, blockposition, (TileEntityHopper) tileentity);
            world.updateAdjacentComparators(blockposition, this);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public static EnumDirection b(int i) {
        return EnumDirection.fromType1(i & 7);
    }

    public static boolean f(int i) {
        return (i & 8) != 8;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockHopper.FACING, b(i)).set(BlockHopper.ENABLED, Boolean.valueOf(f(i)));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockHopper.FACING)).a();

        if (!((Boolean) iblockdata.get(BlockHopper.ENABLED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockHopper.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockHopper.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockHopper.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockHopper.FACING, BlockHopper.ENABLED});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? EnumBlockFaceShape.BOWL : EnumBlockFaceShape.UNDEFINED;
    }
}
