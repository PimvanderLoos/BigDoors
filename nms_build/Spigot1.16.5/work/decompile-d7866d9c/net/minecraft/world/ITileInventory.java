package net.minecraft.world;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ITileEntityContainer;

public interface ITileInventory extends ITileEntityContainer {

    IChatBaseComponent getScoreboardDisplayName();
}
