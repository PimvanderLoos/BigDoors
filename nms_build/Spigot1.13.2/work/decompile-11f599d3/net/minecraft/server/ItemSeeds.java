package net.minecraft.server;

public class ItemSeeds extends Item {

    private final IBlockData a;

    public ItemSeeds(Block block, Item.Info item_info) {
        super(item_info);
        this.a = block.getBlockData();
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition().up();

        if (itemactioncontext.getClickedFace() == EnumDirection.UP && world.isEmpty(blockposition) && this.a.canPlace(world, blockposition)) {
            world.setTypeAndData(blockposition, this.a, 11);
            ItemStack itemstack = itemactioncontext.getItemStack();
            EntityHuman entityhuman = itemactioncontext.getEntity();

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition, itemstack);
            }

            itemstack.subtract(1);
            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }
}
