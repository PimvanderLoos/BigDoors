package net.minecraft.server;

public class ItemSeedFood extends ItemFood {

    private final IBlockData a;

    public ItemSeedFood(int i, float f, Block block, Item.Info item_info) {
        super(i, f, false, item_info);
        this.a = block.getBlockData();
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition().up();

        if (itemactioncontext.getClickedFace() == EnumDirection.UP && world.isEmpty(blockposition) && this.a.canPlace(world, blockposition)) {
            world.setTypeAndData(blockposition, this.a, 11);
            EntityHuman entityhuman = itemactioncontext.getEntity();
            ItemStack itemstack = itemactioncontext.getItemStack();

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition, itemstack);
            }

            itemstack.subtract(1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.PASS;
        }
    }
}
