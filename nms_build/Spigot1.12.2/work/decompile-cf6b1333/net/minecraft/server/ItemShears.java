package net.minecraft.server;

public class ItemShears extends Item {

    public ItemShears() {
        this.d(1);
        this.setMaxDurability(238);
        this.b(CreativeModeTab.i);
    }

    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if (!world.isClientSide) {
            itemstack.damage(1, entityliving);
        }

        Block block = iblockdata.getBlock();

        return iblockdata.getMaterial() != Material.LEAVES && block != Blocks.WEB && block != Blocks.TALLGRASS && block != Blocks.VINE && block != Blocks.TRIPWIRE && block != Blocks.WOOL ? super.a(itemstack, world, iblockdata, blockposition, entityliving) : true;
    }

    public boolean canDestroySpecialBlock(IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        return block == Blocks.WEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
    }

    public float getDestroySpeed(ItemStack itemstack, IBlockData iblockdata) {
        Block block = iblockdata.getBlock();

        return block != Blocks.WEB && iblockdata.getMaterial() != Material.LEAVES ? (block == Blocks.WOOL ? 5.0F : super.getDestroySpeed(itemstack, iblockdata)) : 15.0F;
    }
}
