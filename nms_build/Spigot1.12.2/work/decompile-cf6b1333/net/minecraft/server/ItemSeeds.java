package net.minecraft.server;

public class ItemSeeds extends Item {

    private final Block a;
    private final Block b;

    public ItemSeeds(Block block, Block block1) {
        this.a = block;
        this.b = block1;
        this.b(CreativeModeTab.l);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (enumdirection == EnumDirection.UP && entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack) && world.getType(blockposition).getBlock() == this.b && world.isEmpty(blockposition.up())) {
            world.setTypeUpdate(blockposition.up(), this.a.getBlockData());
            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition.up(), itemstack);
            }

            itemstack.subtract(1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }
}
