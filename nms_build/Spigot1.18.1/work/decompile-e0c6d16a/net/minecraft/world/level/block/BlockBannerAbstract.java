package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBanner;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class BlockBannerAbstract extends BlockTileEntity {

    private final EnumColor color;

    protected BlockBannerAbstract(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.color = enumcolor;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBanner(blockposition, iblockdata, this.color);
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (world.isClientSide) {
            world.getBlockEntity(blockposition, TileEntityTypes.BANNER).ifPresent((tileentitybanner) -> {
                tileentitybanner.fromItem(itemstack);
            });
        } else if (itemstack.hasCustomHoverName()) {
            world.getBlockEntity(blockposition, TileEntityTypes.BANNER).ifPresent((tileentitybanner) -> {
                tileentitybanner.setCustomName(itemstack.getHoverName());
            });
        }

    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

        return tileentity instanceof TileEntityBanner ? ((TileEntityBanner) tileentity).getItem() : super.getCloneItemStack(iblockaccess, blockposition, iblockdata);
    }

    public EnumColor getColor() {
        return this.color;
    }
}
