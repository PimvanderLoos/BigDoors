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
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class BlockBannerAbstract extends BlockTileEntity {

    private final EnumColor a;

    protected BlockBannerAbstract(EnumColor enumcolor, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.a = enumcolor;
    }

    @Override
    public boolean ai_() {
        return true;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBanner(this.a);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBanner) {
                ((TileEntityBanner) tileentity).a(itemstack.getName());
            }
        }

    }

    public EnumColor getColor() {
        return this.a;
    }
}
