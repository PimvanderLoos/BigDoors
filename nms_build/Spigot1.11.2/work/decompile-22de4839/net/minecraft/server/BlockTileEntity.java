package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class BlockTileEntity extends Block implements ITileEntity {

    protected BlockTileEntity(Material material) {
        this(material, material.r());
    }

    protected BlockTileEntity(Material material, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
        this.isTileEntity = true;
    }

    protected boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return world.getType(blockposition.shift(enumdirection)).getMaterial() == Material.CACTUS;
    }

    protected boolean b(World world, BlockPosition blockposition) {
        return this.a(world, blockposition, EnumDirection.NORTH) || this.a(world, blockposition, EnumDirection.SOUTH) || this.a(world, blockposition, EnumDirection.WEST) || this.a(world, blockposition, EnumDirection.EAST);
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        world.s(blockposition);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (tileentity instanceof INamableTileEntity && ((INamableTileEntity) tileentity).hasCustomName()) {
            entityhuman.b(StatisticList.a((Block) this));
            entityhuman.applyExhaustion(0.005F);
            if (world.isClientSide) {
                return;
            }

            int i = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, itemstack);
            Item item = this.getDropType(iblockdata, world.random, i);

            if (item == Items.a) {
                return;
            }

            ItemStack itemstack1 = new ItemStack(item, this.a(world.random));

            itemstack1.g(((INamableTileEntity) tileentity).getName());
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
