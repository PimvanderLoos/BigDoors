package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockWeb extends Block {

    public BlockWeb() {
        super(Material.WEB);
        this.a(CreativeModeTab.c);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        entity.ba();
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockWeb.k;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.STRING;
    }

    protected boolean n() {
        return true;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.a((Block) this));
            a(world, blockposition, new ItemStack(Item.getItemOf(this), 1));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
