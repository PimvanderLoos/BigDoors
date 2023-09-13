package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class BlockBannerAbstract extends BlockTileEntity {

    private final EnumColor a;

    protected BlockBannerAbstract(EnumColor enumcolor, Block.Info block_info) {
        super(block_info);
        this.a = enumcolor;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean a() {
        return true;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityBanner(this.a);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.WHITE_BANNER;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityBanner ? ((TileEntityBanner) tileentity).a(iblockdata) : super.a(iblockaccess, blockposition, iblockdata);
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        a(world, blockposition, this.a((IBlockAccess) world, blockposition, iblockdata));
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (tileentity instanceof TileEntityBanner) {
            a(world, blockposition, ((TileEntityBanner) tileentity).a(iblockdata));
            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, (TileEntity) null, itemstack);
        }

    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityBanner) {
            ((TileEntityBanner) tileentity).a(itemstack, this.a);
        }

    }

    public EnumColor b() {
        return this.a;
    }
}
