package net.minecraft.server;

import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockTileEntity extends Block implements ITileEntity {

    private static final Logger a = LogManager.getLogger();

    protected BlockTileEntity(Block.Info block_info) {
        super(block_info);
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            world.n(blockposition);
        }
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (tileentity instanceof INamableTileEntity && ((INamableTileEntity) tileentity).hasCustomName()) {
            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
            entityhuman.applyExhaustion(0.005F);
            if (world.isClientSide) {
                BlockTileEntity.a.debug("Never going to hit this!");
                return;
            }

            int i = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, itemstack);
            Item item = this.getDropType(iblockdata, world, blockposition, i).getItem();

            if (item == Items.AIR) {
                return;
            }

            ItemStack itemstack1 = new ItemStack(item, this.a(iblockdata, world.random));

            itemstack1.a(((INamableTileEntity) tileentity).getCustomName());
            a(world, blockposition, itemstack1);
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, (TileEntity) null, itemstack);
        }

    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        super.a(iblockdata, world, blockposition, i, j);
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity == null ? false : tileentity.c(i, j);
    }
}
