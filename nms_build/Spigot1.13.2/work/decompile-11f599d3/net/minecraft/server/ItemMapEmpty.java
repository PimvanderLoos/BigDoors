package net.minecraft.server;

public class ItemMapEmpty extends ItemWorldMapBase {

    public ItemMapEmpty(Item.Info item_info) {
        super(item_info);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = ItemWorldMap.createFilledMapView(world, MathHelper.floor(entityhuman.locX), MathHelper.floor(entityhuman.locZ), (byte) 0, true, false);
        ItemStack itemstack1 = entityhuman.b(enumhand);

        itemstack1.subtract(1);
        if (itemstack1.isEmpty()) {
            return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            if (!entityhuman.inventory.pickup(itemstack.cloneItemStack())) {
                entityhuman.drop(itemstack, false);
            }

            entityhuman.b(StatisticList.ITEM_USED.b(this));
            return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack1);
        }
    }
}
