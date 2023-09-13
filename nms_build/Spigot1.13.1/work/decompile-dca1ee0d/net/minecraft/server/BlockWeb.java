package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockWeb extends Block {

    public BlockWeb(Block.Info block_info) {
        super(block_info);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        entity.bh();
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.STRING;
    }

    protected boolean X_() {
        return true;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
            entityhuman.applyExhaustion(0.005F);
            a(world, blockposition, new ItemStack(this));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
