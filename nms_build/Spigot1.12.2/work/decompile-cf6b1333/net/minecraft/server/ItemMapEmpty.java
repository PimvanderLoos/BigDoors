package net.minecraft.server;

public class ItemMapEmpty extends ItemWorldMapBase {

    protected ItemMapEmpty() {
        this.b(CreativeModeTab.f);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = ItemWorldMap.a(world, entityhuman.locX, entityhuman.locZ, (byte) 0, true, false);
        ItemStack itemstack1 = entityhuman.b(enumhand);

        itemstack1.subtract(1);
        if (itemstack1.isEmpty()) {
            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            if (!entityhuman.inventory.pickup(itemstack.cloneItemStack())) {
                entityhuman.drop(itemstack, false);
            }

            entityhuman.b(StatisticList.b((Item) this));
            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack1);
        }
    }
}
