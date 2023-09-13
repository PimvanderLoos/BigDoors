package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockWitherSkullWall extends BlockSkullWall {

    protected BlockWitherSkullWall(BlockBase.Info blockbase_info) {
        super(BlockSkull.Type.WITHER_SKELETON, blockbase_info);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        Blocks.WITHER_SKELETON_SKULL.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
    }
}
