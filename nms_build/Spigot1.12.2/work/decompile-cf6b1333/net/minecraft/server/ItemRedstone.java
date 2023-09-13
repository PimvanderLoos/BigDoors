package net.minecraft.server;

public class ItemRedstone extends Item {

    public ItemRedstone() {
        this.b(CreativeModeTab.d);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        boolean flag = world.getType(blockposition).getBlock().a((IBlockAccess) world, blockposition);
        BlockPosition blockposition1 = flag ? blockposition : blockposition.shift(enumdirection);
        ItemStack itemstack = entityhuman.b(enumhand);

        if (entityhuman.a(blockposition1, enumdirection, itemstack) && world.a(world.getType(blockposition1).getBlock(), blockposition1, false, enumdirection, (Entity) null) && Blocks.REDSTONE_WIRE.canPlace(world, blockposition1)) {
            world.setTypeUpdate(blockposition1, Blocks.REDSTONE_WIRE.getBlockData());
            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition1, itemstack);
            }

            itemstack.subtract(1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }
}
