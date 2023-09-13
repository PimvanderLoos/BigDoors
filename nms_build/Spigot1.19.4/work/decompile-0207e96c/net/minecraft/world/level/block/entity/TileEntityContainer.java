package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
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
    @Nullable
    public IChatBaseComponent name;

    protected TileEntityContainer(TileEntityTypes<?> tileentitytypes, BlockPosition blockposition, IBlockData iblockdata) {
        super(tileentitytypes, blockposition, iblockdata);
        this.lockKey = ChestLock.NO_LOCK;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.lockKey = ChestLock.fromTag(nbttagcompound);
        if (nbttagcompound.contains("CustomName", 8)) {
            this.name = IChatBaseComponent.ChatSerializer.fromJson(nbttagcompound.getString("CustomName"));
        }

    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        this.lockKey.addToTag(nbttagcompound);
        if (this.name != null) {
            nbttagcompound.putString("CustomName", IChatBaseComponent.ChatSerializer.toJson(this.name));
        }

    }

    public void setCustomName(IChatBaseComponent ichatbasecomponent) {
        this.name = ichatbasecomponent;
    }

    @Override
    public IChatBaseComponent getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.name;
    }

    protected abstract IChatBaseComponent getDefaultName();

    public boolean canOpen(EntityHuman entityhuman) {
        return canUnlock(entityhuman, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(EntityHuman entityhuman, ChestLock chestlock, IChatBaseComponent ichatbasecomponent) {
        if (!entityhuman.isSpectator() && !chestlock.unlocksWith(entityhuman.getMainHandItem())) {
            entityhuman.displayClientMessage(IChatBaseComponent.translatable("container.isLocked", ichatbasecomponent), true);
            entityhuman.playNotifySound(SoundEffects.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
        return this.canOpen(entityhuman) ? this.createMenu(i, playerinventory) : null;
    }

    protected abstract Container createMenu(int i, PlayerInventory playerinventory);
}
