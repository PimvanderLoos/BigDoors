package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockSign extends BlockTileEntity {

    protected static final AxisAlignedBB a = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

    protected BlockSign() {
        super(Material.WOOD);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSign.a;
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSign.k;
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
        return new TileEntitySign();
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.SIGN;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.SIGN);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            return tileentity instanceof TileEntitySign ? ((TileEntitySign) tileentity).b(entityhuman) : false;
        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return !this.b(world, blockposition) && super.canPlace(world, blockposition);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
