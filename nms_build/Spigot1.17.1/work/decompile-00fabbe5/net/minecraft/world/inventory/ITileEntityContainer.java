package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;

@FunctionalInterface
public interface ITileEntityContainer {

    @Nullable
    Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman);
}
