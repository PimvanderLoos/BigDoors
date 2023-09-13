package net.minecraft.server;

public class ItemCocoa extends ItemDye {

    public ItemCocoa(EnumColor enumcolor, Item.Info item_info) {
        super(enumcolor, item_info);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        BlockActionContext blockactioncontext = new BlockActionContext(itemactioncontext);

        if (blockactioncontext.b()) {
            World world = itemactioncontext.getWorld();
            IBlockData iblockdata = Blocks.COCOA.getPlacedState(blockactioncontext);
            BlockPosition blockposition = blockactioncontext.getClickPosition();

            if (iblockdata != null && world.setTypeAndData(blockposition, iblockdata, 2)) {
                ItemStack itemstack = itemactioncontext.getItemStack();
                EntityHuman entityhuman = blockactioncontext.getEntity();

                if (entityhuman instanceof EntityPlayer) {
                    CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition, itemstack);
                }

                itemstack.subtract(1);
                return EnumInteractionResult.SUCCESS;
            }
        }

        return EnumInteractionResult.FAIL;
    }
}
