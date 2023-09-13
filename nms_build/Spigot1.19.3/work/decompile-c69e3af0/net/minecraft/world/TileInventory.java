package net.minecraft.world;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ITileEntityContainer;

public final class TileInventory implements ITileInventory {

    private final IChatBaseComponent title;
    private final ITileEntityContainer menuConstructor;

    public TileInventory(ITileEntityContainer itileentitycontainer, IChatBaseComponent ichatbasecomponent) {
        this.menuConstructor = itileentitycontainer;
        this.title = ichatbasecomponent;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return this.title;
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return this.menuConstructor.createMenu(i, playerinventory, entityhuman);
    }
}
