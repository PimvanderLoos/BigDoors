package net.minecraft.world;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ITileEntityContainer;

public final class TileInventory implements ITileInventory {

    private final IChatBaseComponent a;
    private final ITileEntityContainer b;

    public TileInventory(ITileEntityContainer itileentitycontainer, IChatBaseComponent ichatbasecomponent) {
        this.b = itileentitycontainer;
        this.a = ichatbasecomponent;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return this.a;
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return this.b.createMenu(i, playerinventory, entityhuman);
    }
}
