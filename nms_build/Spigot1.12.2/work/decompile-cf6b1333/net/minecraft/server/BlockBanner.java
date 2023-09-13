package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockBanner extends BlockTileEntity {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateInteger ROTATION = BlockStateInteger.of("rotation", 0, 15);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

    protected BlockBanner() {
        super(Material.WOOD);
    }

    public String getName() {
        return LocaleI18n.get("item.banner.white.name");
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockBanner.k;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean d() {
        return true;
    }

    public TileEntity a(World world, int i) {
        return new TileEntityBanner();
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.BANNER;
    }

    private ItemStack c(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityBanner ? ((TileEntityBanner) tileentity).l() : ItemStack.a;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        ItemStack itemstack = this.c(world, blockposition);

        return itemstack.isEmpty() ? new ItemStack(Items.BANNER) : itemstack;
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        ItemStack itemstack = this.c(world, blockposition);

        if (itemstack.isEmpty()) {
            super.dropNaturally(world, blockposition, iblockdata, f, i);
        } else {
            a(world, blockposition, itemstack);
        }

    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return !this.b(world, blockposition) && super.canPlace(world, blockposition);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (tileentity instanceof TileEntityBanner) {
            TileEntityBanner tileentitybanner = (TileEntityBanner) tileentity;
            ItemStack itemstack1 = tileentitybanner.l();

            a(world, blockposition, itemstack1);
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, (TileEntity) null, itemstack);
        }

    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public static class BlockStandingBanner extends BlockBanner {

        public BlockStandingBanner() {
            this.w(this.blockStateList.getBlockData().set(BlockBanner.BlockStandingBanner.ROTATION, Integer.valueOf(0)));
        }

        public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
            return BlockBanner.BlockStandingBanner.c;
        }

        public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
            return iblockdata.set(BlockBanner.BlockStandingBanner.ROTATION, Integer.valueOf(enumblockrotation.a(((Integer) iblockdata.get(BlockBanner.BlockStandingBanner.ROTATION)).intValue(), 16)));
        }

        public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
            return iblockdata.set(BlockBanner.BlockStandingBanner.ROTATION, Integer.valueOf(enumblockmirror.a(((Integer) iblockdata.get(BlockBanner.BlockStandingBanner.ROTATION)).intValue(), 16)));
        }

        public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
            if (!world.getType(blockposition.down()).getMaterial().isBuildable()) {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
            }

            super.a(iblockdata, world, blockposition, block, blockposition1);
        }

        public IBlockData fromLegacyData(int i) {
            return this.getBlockData().set(BlockBanner.BlockStandingBanner.ROTATION, Integer.valueOf(i));
        }

        public int toLegacyData(IBlockData iblockdata) {
            return ((Integer) iblockdata.get(BlockBanner.BlockStandingBanner.ROTATION)).intValue();
        }

        protected BlockStateList getStateList() {
            return new BlockStateList(this, new IBlockState[] { BlockBanner.BlockStandingBanner.ROTATION});
        }
    }

    public static class BlockWallBanner extends BlockBanner {

        protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 0.78125D, 1.0D);
        protected static final AxisAlignedBB e = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.78125D, 0.125D);
        protected static final AxisAlignedBB f = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 0.78125D, 1.0D);
        protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 0.78125D, 1.0D);

        public BlockWallBanner() {
            this.w(this.blockStateList.getBlockData().set(BlockBanner.BlockWallBanner.FACING, EnumDirection.NORTH));
        }

        public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
            return iblockdata.set(BlockBanner.BlockWallBanner.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockBanner.BlockWallBanner.FACING)));
        }

        public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
            return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockBanner.BlockWallBanner.FACING)));
        }

        public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
            switch ((EnumDirection) iblockdata.get(BlockBanner.BlockWallBanner.FACING)) {
            case NORTH:
            default:
                return BlockBanner.BlockWallBanner.d;

            case SOUTH:
                return BlockBanner.BlockWallBanner.e;

            case WEST:
                return BlockBanner.BlockWallBanner.f;

            case EAST:
                return BlockBanner.BlockWallBanner.g;
            }
        }

        public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBanner.BlockWallBanner.FACING);

            if (!world.getType(blockposition.shift(enumdirection.opposite())).getMaterial().isBuildable()) {
                this.b(world, blockposition, iblockdata, 0);
                world.setAir(blockposition);
            }

            super.a(iblockdata, world, blockposition, block, blockposition1);
        }

        public IBlockData fromLegacyData(int i) {
            EnumDirection enumdirection = EnumDirection.fromType1(i);

            if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
                enumdirection = EnumDirection.NORTH;
            }

            return this.getBlockData().set(BlockBanner.BlockWallBanner.FACING, enumdirection);
        }

        public int toLegacyData(IBlockData iblockdata) {
            return ((EnumDirection) iblockdata.get(BlockBanner.BlockWallBanner.FACING)).a();
        }

        protected BlockStateList getStateList() {
            return new BlockStateList(this, new IBlockState[] { BlockBanner.BlockWallBanner.FACING});
        }
    }
}
