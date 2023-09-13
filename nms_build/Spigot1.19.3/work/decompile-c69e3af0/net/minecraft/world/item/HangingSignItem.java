package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.IBlockData;

public class HangingSignItem extends ItemBlockWallable {

    public HangingSignItem(Block block, Block block1, Item.Info item_info) {
        super(block, block1, item_info, EnumDirection.UP);
    }

    @Override
    protected boolean canPlace(IWorldReader iworldreader, IBlockData iblockdata, BlockPosition blockposition) {
        Block block = iblockdata.getBlock();

        if (block instanceof WallHangingSignBlock) {
            WallHangingSignBlock wallhangingsignblock = (WallHangingSignBlock) block;

            if (!wallhangingsignblock.canPlace(iblockdata, iworldreader, blockposition)) {
                return false;
            }
        }

        return super.canPlace(iworldreader, iblockdata, blockposition);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPosition blockposition, World world, @Nullable EntityHuman entityhuman, ItemStack itemstack, IBlockData iblockdata) {
        boolean flag = super.updateCustomBlockEntityTag(blockposition, world, entityhuman, itemstack, iblockdata);

        if (!world.isClientSide && !flag && entityhuman != null) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntitySign) {
                TileEntitySign tileentitysign = (TileEntitySign) tileentity;

                entityhuman.openTextEdit(tileentitysign);
            }
        }

        return flag;
    }
}
