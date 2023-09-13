package net.minecraft.world.entity.npc;

import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.IInventory;

public interface InventoryCarrier {

    @VisibleForDebug
    IInventory getInventory();
}
