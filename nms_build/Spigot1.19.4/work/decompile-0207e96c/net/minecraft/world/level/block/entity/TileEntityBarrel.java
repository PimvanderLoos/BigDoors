package net.minecraft.world.level.block.entity;

import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.ContainerUtil;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockBarrel;
import net.minecraft.world.level.block.state.IBlockData;

public class TileEntityBarrel extends TileEntityLootable {

    private NonNullList<ItemStack> items;
    public final ContainerOpenersCounter openersCounter;

    public TileEntityBarrel(BlockPosition blockposition, IBlockData iblockdata) {
        super(TileEntityTypes.BARREL, blockposition, iblockdata);
        this.items = NonNullList.withSize(27, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityBarrel.this.playSound(iblockdata1, SoundEffects.BARREL_OPEN);
                TileEntityBarrel.this.updateBlockState(iblockdata1, true);
            }

            @Override
            protected void onClose(World world, BlockPosition blockposition1, IBlockData iblockdata1) {
                TileEntityBarrel.this.playSound(iblockdata1, SoundEffects.BARREL_CLOSE);
                TileEntityBarrel.this.updateBlockState(iblockdata1, false);
            }

            @Override
            protected void openerCountChanged(World world, BlockPosition blockposition1, IBlockData iblockdata1, int i, int j) {}

            @Override
            protected boolean isOwnContainer(EntityHuman entityhuman) {
                if (entityhuman.containerMenu instanceof ContainerChest) {
                    IInventory iinventory = ((ContainerChest) entityhuman.containerMenu).getContainer();

                    return iinventory == TileEntityBarrel.this;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    protected void saveAdditional(NBTTagCompound nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        if (!this.trySaveLootTable(nbttagcompound)) {
            ContainerUtil.saveAllItems(nbttagcompound, this.items);
        }

    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbttagcompound)) {
            ContainerUtil.loadAllItems(nbttagcompound, this.items);
        }

    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    @Override
    protected IChatBaseComponent getDefaultName() {
        return IChatBaseComponent.translatable("container.barrel");
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerinventory) {
        return ContainerChest.threeRows(i, playerinventory, this);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.incrementOpeners(entityhuman, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public void stopOpen(EntityHuman entityhuman) {
        if (!this.remove && !entityhuman.isSpectator()) {
            this.openersCounter.decrementOpeners(entityhuman, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void updateBlockState(IBlockData iblockdata, boolean flag) {
        this.level.setBlock(this.getBlockPos(), (IBlockData) iblockdata.setValue(BlockBarrel.OPEN, flag), 3);
    }

    public void playSound(IBlockData iblockdata, SoundEffect soundeffect) {
        BaseBlockPosition baseblockposition = ((EnumDirection) iblockdata.getValue(BlockBarrel.FACING)).getNormal();
        double d0 = (double) this.worldPosition.getX() + 0.5D + (double) baseblockposition.getX() / 2.0D;
        double d1 = (double) this.worldPosition.getY() + 0.5D + (double) baseblockposition.getY() / 2.0D;
        double d2 = (double) this.worldPosition.getZ() + 0.5D + (double) baseblockposition.getZ() / 2.0D;

        this.level.playSound((EntityHuman) null, d0, d1, d2, soundeffect, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}
