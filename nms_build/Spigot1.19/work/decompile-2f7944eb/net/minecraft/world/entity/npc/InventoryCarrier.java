package net.minecraft.world.entity.npc;

import net.minecraft.world.InventorySubcontainer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;

public interface InventoryCarrier {

    InventorySubcontainer getInventory();

    static void pickUpItem(EntityInsentient entityinsentient, InventoryCarrier inventorycarrier, EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItem();

        if (entityinsentient.wantsToPickUp(itemstack)) {
            InventorySubcontainer inventorysubcontainer = inventorycarrier.getInventory();
            boolean flag = inventorysubcontainer.canAddItem(itemstack);

            if (!flag) {
                return;
            }

            entityinsentient.onItemPickup(entityitem);
            int i = itemstack.getCount();
            ItemStack itemstack1 = inventorysubcontainer.addItem(itemstack);

            entityinsentient.take(entityitem, i - itemstack1.getCount());
            if (itemstack1.isEmpty()) {
                entityitem.discard();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }
}
