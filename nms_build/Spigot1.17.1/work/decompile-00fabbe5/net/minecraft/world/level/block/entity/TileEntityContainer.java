package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.ChestLock;
import net.minecraft.world.IInventory;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.level.block.state.IBlockData;

public abstract class TileEntityContainer extends TileEntity implements IInventory, ITileInventory, INamableTileEntity {

    public ChestLock lockKey;
    public IChatBaseComponent name;

    protected TileEntityContainer(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        super(tileentitytypes, blockposition, iblockdata);
        this.lockKey = ChestLock.NO_LOCK;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.lockKey = ChestLock.b(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        this.lockKey.a(nbttagcompound);
        if (this.name != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.name));
        }

        return nbttagcompound;
    }

    public void setCustomName(IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return this.name != null ? this.name : this.getContainerName();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return this.getDisplayName();
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.name;
    }

    protected abstract IChatBaseComponent getContainerName();

    public boolean d(EntityHuman entityhuman) {
        return a(entityhuman, this.lockKey, this.getScoreboardDisplayName());
    }

    public static boolean a(EntityHuman entityhuman, ChestLock chestlock, IChatBaseComponent ichatbasecomponent) {
        if (!entityhuman.isSpectator() && !chestlock.a(entityhuman.getItemInMainHand())) {
            entityhuman.a((IChatBaseComponent) (new ChatMessage("container.isLocked", new Object[]{ichatbasecomponent})), true);
            entityhuman.a(SoundEffects.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return this.d(entityhuman) ? this.createContainer(i, playerinventory) : null;
    }

    protected abstract Container createContainer(int i, PlayerInventory playerinventory);
}
